package com.segundito.app.repository;

import com.segundito.app.entity.Mensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

    @Query("SELECT m FROM Mensaje m WHERE (m.emisor.id = :usuarioId OR m.receptor.id = :usuarioId) AND m.producto.id = :productoId ORDER BY m.fechaEnvio")
    List<Mensaje> findByUsuarioIdAndProductoId(Integer usuarioId, Integer productoId);

    @Query("SELECT DISTINCT m.producto.id FROM Mensaje m WHERE m.emisor.id = :usuarioId OR m.receptor.id = :usuarioId")
    List<Integer> findDistinctProductoIdsByUsuarioId(Integer usuarioId);

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.receptor.id = :usuarioId AND m.leido = false")
    Long countMensajesNoLeidosByUsuario(Integer usuarioId);

    @Modifying
    @Query("UPDATE Mensaje m SET m.leido = true WHERE m.receptor.id = :usuarioId AND m.producto.id = :productoId")
    void marcarComoLeidosByUsuarioAndProducto(Integer usuarioId, Integer productoId);

    Page<Mensaje> findByReceptorIdAndLeidoFalse(Integer receptorId, Pageable pageable);
}