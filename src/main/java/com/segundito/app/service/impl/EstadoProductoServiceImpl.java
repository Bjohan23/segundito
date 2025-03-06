package com.segundito.app.service.impl;

import com.segundito.app.entity.EstadoProducto;
import com.segundito.app.repository.EstadoProductoRepository;
import com.segundito.app.service.EstadoProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EstadoProductoServiceImpl implements EstadoProductoService {

    private final EstadoProductoRepository estadoProductoRepository;

    @Autowired
    public EstadoProductoServiceImpl(EstadoProductoRepository estadoProductoRepository) {
        this.estadoProductoRepository = estadoProductoRepository;
    }

    @Override
    public List<EstadoProducto> listarTodos() {
        return estadoProductoRepository.findAll();
    }

    @Override
    public EstadoProducto buscarPorId(Integer id) {
        return estadoProductoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
    }
}