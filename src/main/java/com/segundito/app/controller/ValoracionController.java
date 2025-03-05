package com.segundito.app.controller;

import com.segundito.app.dto.ValoracionDTO;
import com.segundito.app.dto.ValoracionResponseDTO;
import com.segundito.app.service.ValoracionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/valoraciones")
public class ValoracionController {

    private final ValoracionService valoracionService;

    @Autowired
    public ValoracionController(ValoracionService valoracionService) {
        this.valoracionService = valoracionService;
    }

    @PostMapping
    public ResponseEntity<ValoracionResponseDTO> crearValoracion(
            @Valid @RequestBody ValoracionDTO valoracionDTO,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer compradorId = 1; // TODO: Obtener de autenticación

        ValoracionResponseDTO nuevaValoracion = valoracionService.guardar(valoracionDTO, compradorId);
        return new ResponseEntity<>(nuevaValoracion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ValoracionResponseDTO> actualizarValoracion(
            @PathVariable Integer id,
            @Valid @RequestBody ValoracionDTO valoracionDTO,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer compradorId = 1; // TODO: Obtener de autenticación

        ValoracionResponseDTO valoracionActualizada = valoracionService.actualizar(id, valoracionDTO, compradorId);
        return ResponseEntity.ok(valoracionActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarValoracion(
            @PathVariable Integer id,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer compradorId = 1; // TODO: Obtener de autenticación

        valoracionService.eliminar(id, compradorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValoracionResponseDTO> obtenerValoracion(@PathVariable Integer id) {
        ValoracionResponseDTO valoracion = valoracionService.buscarPorId(id);
        return ResponseEntity.ok(valoracion);
    }

    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<Page<ValoracionResponseDTO>> listarPorVendedor(
            @PathVariable Integer vendedorId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "fechaValoracion") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.Direction.fromString(direction), sort);

        Page<ValoracionResponseDTO> valoraciones = valoracionService.listarPorVendedor(vendedorId, pageable);
        return ResponseEntity.ok(valoraciones);
    }

    @GetMapping("/comprador")
    public ResponseEntity<Page<ValoracionResponseDTO>> listarPorComprador(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "fechaValoracion") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer compradorId = 1; // TODO: Obtener de autenticación

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.Direction.fromString(direction), sort);

        Page<ValoracionResponseDTO> valoraciones = valoracionService.listarPorComprador(compradorId, pageable);
        return ResponseEntity.ok(valoraciones);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Page<ValoracionResponseDTO>> listarPorProducto(
            @PathVariable Integer productoId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "fechaValoracion") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.Direction.fromString(direction), sort);

        Page<ValoracionResponseDTO> valoraciones = valoracionService.listarPorProducto(productoId, pageable);
        return ResponseEntity.ok(valoraciones);
    }

    @GetMapping("/vendedor/{vendedorId}/promedio")
    public ResponseEntity<Map<String, Object>> obtenerPromedioVendedor(@PathVariable Integer vendedorId) {
        Double promedio = valoracionService.obtenerCalificacionPromedio(vendedorId);
        Long totalValoraciones = valoracionService.contarValoraciones(vendedorId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("promedio", promedio);
        respuesta.put("totalValoraciones", totalValoraciones);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Boolean>> verificarExistenciaValoracion(
            @RequestParam Integer productoId,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer compradorId = 1; // TODO: Obtener de autenticación

        boolean existe = valoracionService.existeValoracion(compradorId, productoId);

        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("existe", existe);

        return ResponseEntity.ok(respuesta);
    }
}