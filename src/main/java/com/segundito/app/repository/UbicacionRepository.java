package com.segundito.app.repository;

import com.segundito.app.entity.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    Optional<Ubicacion> findByProvinciaAndCiudad(String provincia, String ciudad);

    List<Ubicacion> findByActivaTrue();

    @Query("SELECT DISTINCT u.provincia FROM Ubicacion u WHERE u.activa = true ORDER BY u.provincia")
    List<String> findDistinctProvincias();

    @Query("SELECT DISTINCT u.ciudad FROM Ubicacion u WHERE u.provincia = :provincia AND u.activa = true ORDER BY u.ciudad")
    List<String> findDistinctCiudadesByProvincia(String provincia);
}