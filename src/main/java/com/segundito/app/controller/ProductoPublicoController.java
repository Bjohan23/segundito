package com.segundito.app.controller;

import com.segundito.app.dto.ProductoResponseDTO;
import com.segundito.app.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos/publicos")
public class ProductoPublicoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoPublicoController(ProductoService productoService) {
        this.productoService = productoService;
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
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(defaultValue = "fechaPublicacion") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.Direction.fromString(direction), sort);

        Page<ProductoResponseDTO> productos = productoService.listarTodos(pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<Page<ProductoResponseDTO>> listarProductosPorCategoria(
            @PathVariable Integer categoriaId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductoResponseDTO> productos = productoService.listarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<ProductoResponseDTO>> buscarProductos(
            @RequestParam String termino,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer size) {

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
            @RequestParam(defaultValue = "12") Integer size) {

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
}