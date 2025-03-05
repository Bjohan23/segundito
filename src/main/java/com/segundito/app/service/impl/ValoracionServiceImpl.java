package com.segundito.app.service.impl;

import com.segundito.app.dto.ValoracionDTO;
import com.segundito.app.dto.ValoracionResponseDTO;
import com.segundito.app.entity.Imagen;
import com.segundito.app.entity.Producto;
import com.segundito.app.entity.Usuario;
import com.segundito.app.entity.Valoracion;
import com.segundito.app.exception.BadRequestException;
import com.segundito.app.exception.ResourceNotFoundException;
import com.segundito.app.repository.ImagenRepository;
import com.segundito.app.repository.ProductoRepository;
import com.segundito.app.repository.UsuarioRepository;
import com.segundito.app.repository.ValoracionRepository;
import com.segundito.app.service.ValoracionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ValoracionServiceImpl implements ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ImagenRepository imagenRepository;

    @Autowired
    public ValoracionServiceImpl(ValoracionRepository valoracionRepository,
                                 UsuarioRepository usuarioRepository,
                                 ProductoRepository productoRepository,
                                 ImagenRepository imagenRepository) {
        this.valoracionRepository = valoracionRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.imagenRepository = imagenRepository;
    }

    @Override
    @Transactional
    public ValoracionResponseDTO guardar(ValoracionDTO valoracionDTO, Integer compradorId) {
        // Verificar que el comprador exista
        Usuario comprador = usuarioRepository.findById(compradorId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprador no encontrado con ID: " + compradorId));

        // Verificar que el vendedor exista
        Usuario vendedor = usuarioRepository.findById(valoracionDTO.getVendedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con ID: " + valoracionDTO.getVendedorId()));

        // Verificar que el producto exista
        Producto producto = productoRepository.findById(valoracionDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + valoracionDTO.getProductoId()));

        // Verificar que el vendedor sea el dueño del producto
        if (!producto.getUsuario().getId().equals(vendedor.getId())) {
            throw new BadRequestException("El vendedor no es el dueño del producto");
        }

        // Verificar que no exista ya una valoración para este comprador y producto
        if (valoracionRepository.existsByCompradorIdAndProductoId(compradorId, valoracionDTO.getProductoId())) {
            throw new BadRequestException("Ya has valorado este producto anteriormente");
        }

        // Crear la valoración
        Valoracion valoracion = new Valoracion();
        valoracion.setCalificacion(valoracionDTO.getCalificacion());
        valoracion.setComentario(valoracionDTO.getComentario());
        valoracion.setFechaValoracion(LocalDateTime.now());
        valoracion.setComprador(comprador);
        valoracion.setVendedor(vendedor);
        valoracion.setProducto(producto);

        Valoracion guardada = valoracionRepository.save(valoracion);

        return convertirADTO(guardada);
    }

    @Override
    @Transactional
    public ValoracionResponseDTO actualizar(Integer id, ValoracionDTO valoracionDTO, Integer compradorId) {
        // Verificar que la valoración exista
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración no encontrada con ID: " + id));

        // Verificar que el usuario sea el dueño de la valoración
        if (!valoracion.getComprador().getId().equals(compradorId)) {
            throw new BadRequestException("No tienes permiso para modificar esta valoración");
        }

        // No se permite cambiar el producto o vendedor, solo la calificación y comentario
        valoracion.setCalificacion(valoracionDTO.getCalificacion());
        valoracion.setComentario(valoracionDTO.getComentario());
        valoracion.setFechaValoracion(LocalDateTime.now());

        Valoracion actualizada = valoracionRepository.save(valoracion);

        return convertirADTO(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Integer id, Integer compradorId) {
        // Verificar que la valoración exista
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración no encontrada con ID: " + id));

        // Verificar que el usuario sea el dueño de la valoración
        if (!valoracion.getComprador().getId().equals(compradorId)) {
            throw new BadRequestException("No tienes permiso para eliminar esta valoración");
        }

        valoracionRepository.deleteById(id);
    }

    @Override
    public ValoracionResponseDTO buscarPorId(Integer id) {
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración no encontrada con ID: " + id));

        return convertirADTO(valoracion);
    }

    @Override
    public Page<ValoracionResponseDTO> listarPorVendedor(Integer vendedorId, Pageable pageable) {
        return valoracionRepository.findByVendedorId(vendedorId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<ValoracionResponseDTO> listarPorComprador(Integer compradorId, Pageable pageable) {
        return valoracionRepository.findByCompradorId(compradorId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<ValoracionResponseDTO> listarPorProducto(Integer productoId, Pageable pageable) {
        // Asumiendo que has creado este método en el repositorio
        return valoracionRepository.findByProductoId(productoId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Double obtenerCalificacionPromedio(Integer vendedorId) {
        Double promedio = valoracionRepository.getCalificacionPromedio(vendedorId);
        return promedio != null ? promedio : 0.0;
    }

    @Override
    public Long contarValoraciones(Integer vendedorId) {
        return valoracionRepository.countByVendedorId(vendedorId);
    }

    @Override
    public boolean existeValoracion(Integer compradorId, Integer productoId) {
        return valoracionRepository.existsByCompradorIdAndProductoId(compradorId, productoId);
    }

    // Método auxiliar para convertir Valoracion a ValoracionResponseDTO
    private ValoracionResponseDTO convertirADTO(Valoracion valoracion) {
        ValoracionResponseDTO dto = new ValoracionResponseDTO();
        dto.setId(valoracion.getId());
        dto.setCalificacion(valoracion.getCalificacion());
        dto.setComentario(valoracion.getComentario());
        dto.setFechaValoracion(valoracion.getFechaValoracion());

        // Datos del comprador
        dto.setCompradorId(valoracion.getComprador().getId());
        dto.setCompradorNombre(valoracion.getComprador().getNombre());
        dto.setCompradorEmail(valoracion.getComprador().getEmail());

        // Datos del vendedor
        dto.setVendedorId(valoracion.getVendedor().getId());
        dto.setVendedorNombre(valoracion.getVendedor().getNombre());
        dto.setVendedorEmail(valoracion.getVendedor().getEmail());

        // Datos del producto
        dto.setProductoId(valoracion.getProducto().getId());
        dto.setProductoTitulo(valoracion.getProducto().getTitulo());

        // Imagen principal del producto
        Optional<Imagen> imagenPrincipal = imagenRepository.findByProductoIdAndEsPrincipalTrue(valoracion.getProducto().getId());
        dto.setProductoImagen(imagenPrincipal.map(Imagen::getUrl).orElse(null));

        return dto;
    }
}