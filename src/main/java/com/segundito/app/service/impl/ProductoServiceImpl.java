package com.segundito.app.service.impl;

import com.segundito.app.dto.ImagenDTO;
import com.segundito.app.dto.ProductoDTO;
import com.segundito.app.dto.ProductoResponseDTO;
import com.segundito.app.entity.*;
import com.segundito.app.exception.BadRequestException;
import com.segundito.app.exception.ResourceNotFoundException;
import com.segundito.app.repository.*;
import com.segundito.app.service.ProductoService;
import com.segundito.app.service.UsuarioService;
import com.segundito.app.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final EstadoProductoRepository estadoProductoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final ImagenRepository imagenRepository;
    private final ValoracionRepository valoracionRepository;
    private final FileUploadUtil fileUploadUtil;
    private  final FavoritoRepository favoritoRepository;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository,
                               UsuarioRepository usuarioRepository,
                               CategoriaRepository categoriaRepository,
                               EstadoProductoRepository estadoProductoRepository,
                               UbicacionRepository ubicacionRepository,
                               ImagenRepository imagenRepository,
                               ValoracionRepository valoracionRepository,
                               FileUploadUtil fileUploadUtil,
                               FavoritoRepository  favoritoRepository
                                                            ) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.estadoProductoRepository = estadoProductoRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.imagenRepository = imagenRepository;
        this.valoracionRepository = valoracionRepository;
        this.fileUploadUtil = fileUploadUtil;
        this.favoritoRepository = favoritoRepository;
    }

    @Override
    @Transactional
    public ProductoResponseDTO guardar(ProductoDTO productoDTO, Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + productoDTO.getCategoriaId()));

        EstadoProducto estado = estadoProductoRepository.findById(productoDTO.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + productoDTO.getEstadoId()));

        Ubicacion ubicacion = ubicacionRepository.findById(productoDTO.getUbicacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con ID: " + productoDTO.getUbicacionId()));

        Producto producto = new Producto();
        producto.setTitulo(productoDTO.getTitulo());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setFechaModificacion(LocalDateTime.now());
        producto.setUsuario(usuario);
        producto.setCategoria(categoria);
        producto.setEstado(estado);
        producto.setUbicacion(ubicacion);
        producto.setDestacado(productoDTO.getDestacado());
        producto.setVendido(productoDTO.getVendido());
        producto.setVisitas(0);

        Producto guardado = productoRepository.save(producto);

        return convertirADTO(guardado);
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizar(Integer id, ProductoDTO productoDTO, Integer usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Verificar que el usuario sea el propietario del producto
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para editar este producto");
        }

        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + productoDTO.getCategoriaId()));

        EstadoProducto estado = estadoProductoRepository.findById(productoDTO.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado con ID: " + productoDTO.getEstadoId()));

        Ubicacion ubicacion = ubicacionRepository.findById(productoDTO.getUbicacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con ID: " + productoDTO.getUbicacionId()));

        producto.setTitulo(productoDTO.getTitulo());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setFechaModificacion(LocalDateTime.now());
        producto.setCategoria(categoria);
        producto.setEstado(estado);
        producto.setUbicacion(ubicacion);
        producto.setDestacado(productoDTO.getDestacado());
        producto.setVendido(productoDTO.getVendido());

        Producto actualizado = productoRepository.save(producto);

        return convertirADTO(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Integer id, Integer usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Verificar que el usuario sea el propietario del producto
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para eliminar este producto");
        }

        // Eliminar imágenes asociadas del sistema de archivos
        List<Imagen> imagenes = imagenRepository.findByProductoIdOrderByOrdenAsc(id);
        for (Imagen imagen : imagenes) {
            fileUploadUtil.deleteFile(imagen.getUrl());
        }

        productoRepository.deleteById(id);
    }

    @Override
    public ProductoResponseDTO buscarPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        return convertirADTO(producto);
    }

    @Override
    public Page<ProductoResponseDTO> listarTodos(Pageable pageable) {
        return productoRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<ProductoResponseDTO> listarPorUsuario(Integer usuarioId, Pageable pageable) {
        return productoRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<ProductoResponseDTO> listarPorCategoria(Integer categoriaId, Pageable pageable) {
        return productoRepository.findByCategoriaId(categoriaId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<ProductoResponseDTO> listarPorUbicacion(String provincia, String ciudad, Pageable pageable) {
        return productoRepository.findByUbicacionProvinciaAndUbicacionCiudad(provincia, ciudad, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<ProductoResponseDTO> buscarPorTermino(String termino, Pageable pageable) {
        return productoRepository.buscarPorTermino(termino, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public Page<ProductoResponseDTO> buscarPorCategoriaYRangoPrecio(
            Integer categoriaId, BigDecimal precioMin, BigDecimal precioMax, Pageable pageable) {
        return productoRepository.buscarPorCategoriaYRangoPrecio(categoriaId, precioMin, precioMax, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public List<ProductoResponseDTO> listarDestacados(int cantidad) {
        return productoRepository.findDestacados(Pageable.ofSize(cantidad))
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> listarMasPopulares() {
        return productoRepository.findMasPopulares()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductoResponseDTO> listarRecientes(Pageable pageable) {
        return productoRepository.findRecientes(pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional
    public void marcarComoVendido(Integer id, Integer usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Verificar que el usuario sea el propietario del producto
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para modificar este producto");
        }

        producto.setVendido(true);
        producto.setFechaModificacion(LocalDateTime.now());

        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void destacarProducto(Integer id, Integer usuarioId, Boolean destacado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Verificar que el usuario sea el propietario del producto
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para modificar este producto");
        }

        producto.setDestacado(destacado);
        producto.setFechaModificacion(LocalDateTime.now());

        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void incrementarVisitas(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        producto.setVisitas(producto.getVisitas() + 1);

        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void agregarImagenes(Integer id, List<MultipartFile> imagenes, Integer usuarioId) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        // Verificar que el usuario sea el propietario del producto
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para modificar este producto");
        }

        // Limitar número máximo de imágenes (por ejemplo, 5)
        List<Imagen> imagenesExistentes = imagenRepository.findByProductoIdOrderByOrdenAsc(id);
        if (imagenesExistentes.size() + imagenes.size() > 5) {
            throw new BadRequestException("No se pueden agregar más de 5 imágenes por producto");
        }

        // Determinar el próximo orden
        int ordenInicial = imagenesExistentes.isEmpty() ? 1 :
                imagenesExistentes.stream()
                        .mapToInt(Imagen::getOrden)
                        .max()
                        .orElse(0) + 1;

        // Determinar si será imagen principal
        boolean tieneImagenPrincipal = imagenesExistentes.stream()
                .anyMatch(Imagen::getEsPrincipal);

        try {
            int orden = ordenInicial;
            for (MultipartFile archivo : imagenes) {
                // Subir la imagen
                String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
                String rutaImagen = fileUploadUtil.saveFile("productos/" + id, nombreArchivo, archivo);

                // Crear registro en la base de datos
                Imagen imagen = new Imagen();
                imagen.setProducto(producto);
                imagen.setUrl(rutaImagen);
                imagen.setOrden(orden++);

                // Si no hay imagen principal, establecer la primera como principal
                if (!tieneImagenPrincipal && orden == ordenInicial + 1) {
                    imagen.setEsPrincipal(true);
                    tieneImagenPrincipal = true;
                } else {
                    imagen.setEsPrincipal(false);
                }

                imagenRepository.save(imagen);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al subir imágenes", e);
        }
    }

    @Override
    @Transactional
    public void eliminarImagen(Integer productoId, Integer imagenId, Integer usuarioId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        // Verificar que el usuario sea el propietario del producto
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para modificar este producto");
        }

        Imagen imagen = imagenRepository.findById(imagenId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + imagenId));

        // Verificar que la imagen pertenezca al producto
        if (!imagen.getProducto().getId().equals(productoId)) {
            throw new BadRequestException("La imagen no pertenece a este producto");
        }

        // Eliminar archivo del sistema
        fileUploadUtil.deleteFile(imagen.getUrl());

        // Si era la imagen principal, establecer otra como principal
        if (imagen.getEsPrincipal()) {
            List<Imagen> otrasImagenes = imagenRepository.findByProductoIdOrderByOrdenAsc(productoId);
            otrasImagenes.removeIf(img -> img.getId().equals(imagenId));

            if (!otrasImagenes.isEmpty()) {
                Imagen nuevaPrincipal = otrasImagenes.get(0);
                nuevaPrincipal.setEsPrincipal(true);
                imagenRepository.save(nuevaPrincipal);
            }
        }

        // Eliminar de la base de datos
        imagenRepository.deleteById(imagenId);
    }

    @Override
    @Transactional
    public void establecerImagenPrincipal(Integer productoId, Integer imagenId, Integer usuarioId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productoId));

        // Verificar que el usuario sea el propietario del producto
        if (!producto.getUsuario().getId().equals(usuarioId)) {
            throw new BadRequestException("No tienes permiso para modificar este producto");
        }

        Imagen imagen = imagenRepository.findById(imagenId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagen no encontrada con ID: " + imagenId));

        // Verificar que la imagen pertenezca al producto
        if (!imagen.getProducto().getId().equals(productoId)) {
            throw new BadRequestException("La imagen no pertenece a este producto");
        }

        // Quitar imagen principal actual
        imagenRepository.quitarImagenPrincipal(productoId);

        // Establecer nueva imagen principal
        imagen.setEsPrincipal(true);
        imagenRepository.save(imagen);
    }

    // Método auxiliar para convertir Producto a ProductoResponseDTO
    private ProductoResponseDTO convertirADTO(Producto producto) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(producto.getId());
        dto.setTitulo(producto.getTitulo());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setFechaPublicacion(producto.getFechaPublicacion());
        dto.setFechaModificacion(producto.getFechaModificacion());

        // Datos de usuario
        dto.setUsuarioId(producto.getUsuario().getId());
        dto.setUsuarioNombre(producto.getUsuario().getNombre());
        dto.setUsuarioTelefono(producto.getUsuario().getTelefono());
        dto.setUsuarioEmail(producto.getUsuario().getEmail());

        // Datos de categoría
        dto.setCategoriaId(producto.getCategoria().getId());
        dto.setCategoriaNombre(producto.getCategoria().getNombre());

        // Datos de estado
        dto.setEstadoId(producto.getEstado().getId());
        dto.setEstadoNombre(producto.getEstado().getNombre());

        // Datos de ubicación
        dto.setUbicacionId(producto.getUbicacion().getId());
        dto.setUbicacionProvincia(producto.getUbicacion().getProvincia());
        dto.setUbicacionCiudad(producto.getUbicacion().getCiudad());

        // Otros datos
        dto.setDestacado(producto.getDestacado());
        dto.setVendido(producto.getVendido());
        dto.setVisitas(producto.getVisitas());
        dto.setUsuarioFotoPerfil(producto.getUsuario().getFotoPerfil());

        // Añadir la propiedad esFavorito
        dto.setEsFavorito(false); // Por defecto, no es favorito

        // Si tienes implementación de favoritos, puedes usar algo como esto:
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName())) {
            try {
                String userEmail = authentication.getName();
                Usuario usuario = usuarioRepository.findByEmail(userEmail).orElse(null);
                if (usuario != null) {
                    boolean esFav = favoritoRepository.existsByUsuarioIdAndProductoId(usuario.getId(), producto.getId());
                    dto.setEsFavorito(esFav);
                }
            } catch (Exception e) {
                System.err.println("Error al verificar favorito: " + e.getMessage());
            }
        }

        // Imágenes: asegúrate de que las URLs sean absolutas
        List<Imagen> imagenes = imagenRepository.findByProductoIdOrderByOrdenAsc(producto.getId());
        List<ImagenDTO> imagenesDTO = new ArrayList<>();

        if (imagenes != null) {
            for (Imagen imagen : imagenes) {
                ImagenDTO imagenDTO = new ImagenDTO();
                imagenDTO.setId(imagen.getId());

                // Asegurarse de que la URL es correcta
                String url = imagen.getUrl();
                // No añadir "uploads/" si ya está incluido
                if (url != null && !url.startsWith("/")) {
                    url = url.replace("\\", "/"); // Normalizar separadores de Windows
                }

                imagenDTO.setUrl(url);
                imagenDTO.setEsPrincipal(imagen.getEsPrincipal());
                imagenDTO.setOrden(imagen.getOrden());
                imagenesDTO.add(imagenDTO);
            }
        }

        dto.setImagenes(imagenesDTO);

        // Imagen principal: mismo proceso de normalización
        Optional<Imagen> imagenPrincipal = imagenes.stream()
                .filter(Imagen::getEsPrincipal)
                .findFirst();

        if (imagenPrincipal.isPresent()) {
            String url = imagenPrincipal.get().getUrl();
            if (url != null && !url.startsWith("/")) {
                url = url.replace("\\", "/");
            }
            dto.setImagenPrincipal(url);
        } else {
            dto.setImagenPrincipal(null);
        }

        // Calificación del vendedor
        dto.setCalificacionVendedor(valoracionRepository.getCalificacionPromedio(producto.getUsuario().getId()));
        dto.setTotalValoraciones(valoracionRepository.countByVendedorId(producto.getUsuario().getId()));

        return dto;
    }

    // Método auxiliar para convertir Imagen a ImagenDTO
    private ImagenDTO convertirImagenADTO(Imagen imagen) {
        ImagenDTO dto = new ImagenDTO();
        dto.setId(imagen.getId());
        dto.setUrl(imagen.getUrl());
        dto.setEsPrincipal(imagen.getEsPrincipal());
        dto.setOrden(imagen.getOrden());
        return dto;
    }
}