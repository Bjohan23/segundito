package com.segundito.app.repository;

import com.segundito.app.entity.Valoracion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Integer> {

    Page<Valoracion> findByVendedorId(Integer vendedorId, Pageable pageable);

    Page<Valoracion> findByCompradorId(Integer compradorId, Pageable pageable);

    Page<Valoracion> findByProductoId(Integer productoId, Pageable pageable);

    @Query("SELECT AVG(v.calificacion) FROM Valoracion v WHERE v.vendedor.id = :vendedorId")
    Double getCalificacionPromedio(Integer vendedorId);

    @Query("SELECT COUNT(v) FROM Valoracion v WHERE v.vendedor.id = :vendedorId")
    Long countByVendedorId(Integer vendedorId);

    boolean existsByCompradorIdAndProductoId(Integer compradorId, Integer productoId);
}