package com.segundito.app.repository;

import com.segundito.app.entity.Historial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Integer> {

    Page<Historial> findByUsuarioId(Integer usuarioId, Pageable pageable);

    Page<Historial> findByEntidadAndEntidadId(String entidad, Integer entidadId, Pageable pageable);
}