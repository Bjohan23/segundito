package com.segundito.app.service;

import com.segundito.app.dto.ProductoDTO;
import com.segundito.app.dto.ProductoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoService {

    ProductoResponseDTO guardar(ProductoDTO productoDTO, Integer usuarioId);

    ProductoResponseDTO actualizar(Integer id, ProductoDTO productoDTO, Integer usuarioId);

    void eliminar(Integer id, Integer usuarioId);

    ProductoResponseDTO buscarPorId(Integer id);

    Page<ProductoResponseDTO> listarTodos(Pageable pageable);

    Page<ProductoResponseDTO> listarPorUsuario(Integer usuarioId, Pageable pageable);

    Page<ProductoResponseDTO> listarPorCategoria(Integer categoriaId, Pageable pageable);

    Page<ProductoResponseDTO> listarPorUbicacion(String provincia, String ciudad, Pageable pageable);

    Page<ProductoResponseDTO> buscarPorTermino(String termino, Pageable pageable);

    Page<ProductoResponseDTO> buscarPorCategoriaYRangoPrecio(Integer categoriaId, BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);

    List<ProductoResponseDTO> listarDestacados(int cantidad);

    List<ProductoResponseDTO> listarMasPopulares();

    Page<ProductoResponseDTO> listarRecientes(Pageable pageable);

    Page<ProductoResponseDTO> buscarPorTerminoYCategoria(String termino, Integer categoriaId, Pageable pageable);

    Page<ProductoResponseDTO> buscarPorCategoriaTerminoYPrecio(Integer categoriaId, String termino,
                                                               BigDecimal precioMin, BigDecimal precioMax,
                                                               Pageable pageable);

    void marcarComoVendido(Integer id, Integer usuarioId);

    void destacarProducto(Integer id, Integer usuarioId, Boolean destacado);

    void incrementarVisitas(Integer id);

    void agregarImagenes(Integer id, List<MultipartFile> imagenes, Integer usuarioId);

    void eliminarImagen(Integer productoId, Integer imagenId, Integer usuarioId);

    void establecerImagenPrincipal(Integer productoId, Integer imagenId, Integer usuarioId);
}