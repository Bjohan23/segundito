package com.segundito.app.repository;

import com.segundito.app.entity.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Integer> {

    List<Imagen> findByProductoIdOrderByOrdenAsc(Integer productoId);

    Optional<Imagen> findByProductoIdAndEsPrincipalTrue(Integer productoId);

    @Modifying
    @Query("UPDATE Imagen i SET i.esPrincipal = false WHERE i.producto.id = :productoId")
    void quitarImagenPrincipal(Integer productoId);

    void deleteByProductoId(Integer productoId);
}