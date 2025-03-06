package com.segundito.app.service.impl;

import com.segundito.app.entity.Ubicacion;
import com.segundito.app.repository.UbicacionRepository;
import com.segundito.app.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;

    @Autowired
    public UbicacionServiceImpl(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    public List<Ubicacion> listarTodas() {
        return ubicacionRepository.findAll();
    }
}