package com.segundito.app.service.impl;

import com.segundito.app.dto.MensajeDTO;
import com.segundito.app.dto.MensajeResponseDTO;
import com.segundito.app.entity.Imagen;
import com.segundito.app.entity.Mensaje;
import com.segundito.app.entity.Producto;
import com.segundito.app.entity.Usuario;
import com.segundito.app.exception.BadRequestException;
import com.segundito.app.exception.ResourceNotFoundException;
import com.segundito.app.repository.ImagenRepository;
import com.segundito.app.repository.MensajeRepository;
import com.segundito.app.repository.ProductoRepository;
import com.segundito.app.repository.UsuarioRepository;
import com.segundito.app.service.MensajeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ImagenRepository imagenRepository;

    @Autowired
    public MensajeServiceImpl(MensajeRepository mensajeRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoRepository productoRepository,
                              ImagenRepository imagenRepository) {
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.imagenRepository = imagenRepository;
    }

    @Override
    @Transactional
    public MensajeResponseDTO enviar(MensajeDTO mensajeDTO, Integer emisorId) {
        // Verificar que el emisor exista
        Usuario emisor = usuarioRepository.findById(emisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario emisor no encontrado con ID: " + emisorId));

        // Verificar que el receptor exista
        Usuario receptor = usuarioRepository.findById(mensajeDTO.getReceptorId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario receptor no encontrado con ID: " + mensajeDTO.getReceptorId()));

        // Verificar que el producto exista
        Producto producto = productoRepository.findById(mensajeDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + mensajeDTO.getProductoId()));

        // No permitir enviar mensajes a uno mismo
        if (emisorId.equals(mensajeDTO.getReceptorId())) {
            throw new BadRequestException("No puedes enviarte mensajes a ti mismo");
        }

        // Crear el mensaje
        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(mensajeDTO.getContenido());
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setLeido(false);
        mensaje.setEmisor(emisor);
        mensaje.setReceptor(receptor);
        mensaje.setProducto(producto);

        Mensaje guardado = mensajeRepository.save(mensaje);

        return convertirADTO(guardado);
    }

    @Override
    public MensajeResponseDTO buscarPorId(Integer id, Integer usuarioId) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje no encontrado con ID: " + id));

        // Verificar que el usuario sea el emisor o receptor del mensaje
        if (!mensaje.getEmisor().getId().equals(usuarioId) && !mensaje.getReceptor().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para ver este mensaje");
        }

        // Si el usuario es el receptor, marcar como leído
        if (mensaje.getReceptor().getId().equals(usuarioId) && !mensaje.getLeido()) {
            mensaje.setLeido(true);
            mensajeRepository.save(mensaje);
        }

        return convertirADTO(mensaje);
    }

    @Override
    public List<MensajeResponseDTO> listarConversacion(Integer productoId, Integer usuarioId) {
        // Verificar que el producto exista
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + productoId);
        }

        // Obtener todos los mensajes relacionados con el producto y el usuario
        List<Mensaje> mensajes = mensajeRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);

        // Marcar como leídos los mensajes recibidos
        for (Mensaje mensaje : mensajes) {
            if (mensaje.getReceptor().getId().equals(usuarioId) && !mensaje.getLeido()) {
                mensaje.setLeido(true);
            }
        }
        mensajeRepository.saveAll(mensajes);

        return mensajes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MensajeResponseDTO> listarNoLeidos(Integer usuarioId, Pageable pageable) {
        return mensajeRepository.findByReceptorIdAndLeidoFalse(usuarioId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Long contarNoLeidos(Integer usuarioId) {
        return mensajeRepository.countMensajesNoLeidosByUsuario(usuarioId);
    }

    @Override
    public Map<Integer, String> listarConversacionesActivas(Integer usuarioId) {
        // Obtener todos los productos con los que el usuario ha tenido conversaciones
        List<Integer> productosIds = mensajeRepository.findDistinctProductoIdsByUsuarioId(usuarioId);

        // Crear un mapa con el ID del producto y su título
        Map<Integer, String> conversaciones = new HashMap<>();
        for (Integer productoId : productosIds) {
            Producto producto = productoRepository.findById(productoId)
                    .orElse(null);

            if (producto != null) {
                conversaciones.put(productoId, producto.getTitulo());
            }
        }

        return conversaciones;
    }

    @Override
    @Transactional
    public void marcarComoLeidos(Integer productoId, Integer usuarioId) {
        mensajeRepository.marcarComoLeidosByUsuarioAndProducto(usuarioId, productoId);
    }

    // Método auxiliar para convertir Mensaje a MensajeResponseDTO
    private MensajeResponseDTO convertirADTO(Mensaje mensaje) {
        MensajeResponseDTO dto = new MensajeResponseDTO();
        dto.setId(mensaje.getId());
        dto.setContenido(mensaje.getContenido());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setLeido(mensaje.getLeido());

        // Datos del emisor
        dto.setEmisorId(mensaje.getEmisor().getId());
        dto.setEmisorNombre(mensaje.getEmisor().getNombre());
        dto.setEmisorEmail(mensaje.getEmisor().getEmail());

        // Datos del receptor
        dto.setReceptorId(mensaje.getReceptor().getId());
        dto.setReceptorNombre(mensaje.getReceptor().getNombre());
        dto.setReceptorEmail(mensaje.getReceptor().getEmail());

        // Datos del producto
        dto.setProductoId(mensaje.getProducto().getId());
        dto.setProductoTitulo(mensaje.getProducto().getTitulo());

        // Imagen principal del producto
        Optional<Imagen> imagenPrincipal = imagenRepository.findByProductoIdAndEsPrincipalTrue(mensaje.getProducto().getId());
        dto.setProductoImagen(imagenPrincipal.map(Imagen::getUrl).orElse(null));

        return dto;
    }
}