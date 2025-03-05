package com.segundito.app.repository;

import com.segundito.app.entity.EstadoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoProductoRepository extends JpaRepository<EstadoProducto, Integer> {
    Optional<EstadoProducto> findByNombre(String nombre);
}