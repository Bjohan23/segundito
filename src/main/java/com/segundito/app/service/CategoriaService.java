package com.segundito.app.service;

import com.segundito.app.dto.CategoriaDTO;
import com.segundito.app.dto.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {

    CategoriaResponseDTO guardar(CategoriaDTO categoriaDTO);

    CategoriaResponseDTO actualizar(Integer id, CategoriaDTO categoriaDTO);

    void eliminar(Integer id);

    CategoriaResponseDTO buscarPorId(Integer id);

    List<CategoriaResponseDTO> listarTodas();

    List<CategoriaResponseDTO> listarPrincipales();

    List<CategoriaResponseDTO> listarSubcategorias(Integer categoriaPadreId);

    List<CategoriaResponseDTO> listarArbol();

    void activarDesactivar(Integer id, Boolean activa);
}