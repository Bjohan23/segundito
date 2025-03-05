package com.segundito.app.repository;

import com.segundito.app.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    Optional<Categoria> findByNombre(String nombre);
    List<Categoria> findByCategoriaPadreIsNull();
    List<Categoria> findByCategoriaPadreId(Integer categoriaPadreId);
    List<Categoria> findByActivaTrue();
}