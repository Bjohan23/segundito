package com.segundito.app.controller;

import com.segundito.app.dto.CategoriaDTO;
import com.segundito.app.dto.CategoriaResponseDTO;
import com.segundito.app.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaResponseDTO nuevaCategoria = categoriaService.guardar(categoriaDTO);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaDTO categoriaDTO) {

        CategoriaResponseDTO categoriaActualizada = categoriaService.actualizar(id, categoriaDTO);
        return ResponseEntity.ok(categoriaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Integer id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoria(@PathVariable Integer id) {
        CategoriaResponseDTO categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/principales")
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategoriasPrincipales() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarPrincipales();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/subcategorias/{categoriaPadreId}")
    public ResponseEntity<List<CategoriaResponseDTO>> listarSubcategorias(@PathVariable Integer categoriaPadreId) {
        List<CategoriaResponseDTO> subcategorias = categoriaService.listarSubcategorias(categoriaPadreId);
        return ResponseEntity.ok(subcategorias);
    }

    @GetMapping("/arbol")
    public ResponseEntity<List<CategoriaResponseDTO>> listarArbolCategorias() {
        List<CategoriaResponseDTO> arbolCategorias = categoriaService.listarArbol();
        return ResponseEntity.ok(arbolCategorias);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Map<String, String>> activarDesactivarCategoria(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> estado) {

        Boolean activa = estado.get("activa");
        if (activa == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El campo 'activa' es requerido"));
        }

        categoriaService.activarDesactivar(id, activa);

        String mensaje = activa ? "Categoría activada con éxito" : "Categoría desactivada con éxito";
        return ResponseEntity.ok(Map.of("mensaje", mensaje));
    }
}