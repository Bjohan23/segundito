package com.segundito.app.controller;

import com.segundito.app.dto.ImagenDTO;
import com.segundito.app.dto.ProductoDTO;
import com.segundito.app.dto.ProductoResponseDTO;
import com.segundito.app.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody ProductoDTO productoDTO,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        // Por ahora, simulamos un ID de usuario para pruebas
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        ProductoResponseDTO nuevoProducto = productoService.guardar(productoDTO, usuarioId);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoDTO productoDTO,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        ProductoResponseDTO productoActualizado = productoService.actualizar(id, productoDTO, usuarioId);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Integer id,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        productoService.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProducto(@PathVariable Integer id) {
        // Incrementar contador de visitas
        productoService.incrementarVisitas(id);

        ProductoResponseDTO producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping
    public ResponseEntity<Page<ProductoResponseDTO>> listarProductos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "fechaPublicacion") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.Direction.fromString(direction), sort);

        Page<ProductoResponseDTO> productos = productoService.listarTodos(pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<ProductoResponseDTO>> listarProductosPorUsuario(
            @PathVariable Integer usuarioId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productos = productoService.listarPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<Page<ProductoResponseDTO>> listarProductosPorCategoria(
            @PathVariable Integer categoriaId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productos = productoService.listarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/ubicacion")
    public ResponseEntity<Page<ProductoResponseDTO>> listarProductosPorUbicacion(
            @RequestParam String provincia,
            @RequestParam String ciudad,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productos = productoService.listarPorUbicacion(provincia, ciudad, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<ProductoResponseDTO>> buscarProductos(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productos = productoService.buscarPorTermino(termino, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<ProductoResponseDTO>> filtrarProductos(
            @RequestParam Integer categoriaId,
            @RequestParam(defaultValue = "0") BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // Si no se especifica precio máximo, usar un valor muy alto
        if (precioMax == null) {
            precioMax = new BigDecimal("999999999");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productos = productoService.buscarPorCategoriaYRangoPrecio(
                categoriaId, precioMin, precioMax, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/destacados")
    public ResponseEntity<List<ProductoResponseDTO>> listarDestacados(
            @RequestParam(defaultValue = "6") Integer cantidad) {

        List<ProductoResponseDTO> productos = productoService.listarDestacados(cantidad);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/populares")
    public ResponseEntity<List<ProductoResponseDTO>> listarPopulares() {
        List<ProductoResponseDTO> productos = productoService.listarMasPopulares();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/recientes")
    public ResponseEntity<Page<ProductoResponseDTO>> listarRecientes(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productos = productoService.listarRecientes(pageable);
        return ResponseEntity.ok(productos);
    }

    @PatchMapping("/{id}/vendido")
    public ResponseEntity<Void> marcarComoVendido(
            @PathVariable Integer id,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        productoService.marcarComoVendido(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/destacado")
    public ResponseEntity<Void> destacarProducto(
            @PathVariable Integer id,
            @RequestParam Boolean destacado,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        productoService.destacarProducto(id, usuarioId, destacado);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/imagenes")
    public ResponseEntity<Map<String, String>> subirImagenes(
            @PathVariable Integer id,
            @RequestParam("files") List<MultipartFile> files,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        productoService.agregarImagenes(id, files, usuarioId);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Imágenes subidas correctamente");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productoId}/imagenes/{imagenId}")
    public ResponseEntity<Void> eliminarImagen(
            @PathVariable Integer productoId,
            @PathVariable Integer imagenId,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        productoService.eliminarImagen(productoId, imagenId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productoId}/imagenes/{imagenId}/principal")
    public ResponseEntity<Void> establecerImagenPrincipal(
            @PathVariable Integer productoId,
            @PathVariable Integer imagenId,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        productoService.establecerImagenPrincipal(productoId, imagenId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}