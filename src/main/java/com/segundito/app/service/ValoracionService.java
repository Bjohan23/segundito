package com.segundito.app.service;

import com.segundito.app.dto.ValoracionDTO;
import com.segundito.app.dto.ValoracionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ValoracionService {

    ValoracionResponseDTO guardar(ValoracionDTO valoracionDTO, Integer compradorId);

    ValoracionResponseDTO actualizar(Integer id, ValoracionDTO valoracionDTO, Integer compradorId);

    void eliminar(Integer id, Integer compradorId);

    ValoracionResponseDTO buscarPorId(Integer id);

    Page<ValoracionResponseDTO> listarPorVendedor(Integer vendedorId, Pageable pageable);

    Page<ValoracionResponseDTO> listarPorComprador(Integer compradorId, Pageable pageable);

    Page<ValoracionResponseDTO> listarPorProducto(Integer productoId, Pageable pageable);

    Double obtenerCalificacionPromedio(Integer vendedorId);

    Long contarValoraciones(Integer vendedorId);

    boolean existeValoracion(Integer compradorId, Integer productoId);
}