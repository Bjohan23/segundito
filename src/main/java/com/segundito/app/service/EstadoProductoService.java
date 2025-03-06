package com.segundito.app.service;

import com.segundito.app.entity.EstadoProducto;
import java.util.List;

public interface EstadoProductoService {
    List<EstadoProducto> listarTodos();
    EstadoProducto buscarPorId(Integer id);
    // otros métodos necesarios
}