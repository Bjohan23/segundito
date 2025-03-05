package com.segundito.app.repository;

import com.segundito.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:termino% OR u.email LIKE %:termino%")
    List<Usuario> buscarPorTermino(String termino);

    @Query(value = "SELECT u.* FROM usuarios u INNER JOIN " +
            "(SELECT v.vendedor_id, AVG(v.calificacion) as promedio FROM valoraciones v " +
            "GROUP BY v.vendedor_id ORDER BY promedio DESC LIMIT 10) " +
            "AS mejores ON u.id = mejores.vendedor_id", nativeQuery = true)
    List<Usuario> findTopVendedores();
}