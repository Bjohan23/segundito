package com.segundito.app.repository;

import com.segundito.app.entity.Favorito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {

    Page<Favorito> findByUsuarioId(Integer usuarioId, Pageable pageable);

    Optional<Favorito> findByUsuarioIdAndProductoId(Integer usuarioId, Integer productoId);

    boolean existsByUsuarioIdAndProductoId(Integer usuarioId, Integer productoId);

    void deleteByUsuarioIdAndProductoId(Integer usuarioId, Integer productoId);

    Long countByProductoId(Integer productoId);
}