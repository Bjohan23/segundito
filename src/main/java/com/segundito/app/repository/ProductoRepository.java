package com.segundito.app.repository;

import com.segundito.app.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Page<Producto> findByCategoriaId(Integer categoriaId, Pageable pageable);

    Page<Producto> findByUsuarioId(Integer usuarioId, Pageable pageable);

    Page<Producto> findByUbicacionProvinciaAndUbicacionCiudad(String provincia, String ciudad, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.vendido = false AND " +
            "(p.titulo LIKE %:termino% OR p.descripcion LIKE %:termino%)")
    Page<Producto> buscarPorTermino(@Param("termino") String termino, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.vendido = false AND p.categoria.id = :categoriaId " +
            "AND p.precio BETWEEN :precioMin AND :precioMax")
    Page<Producto> buscarPorCategoriaYRangoPrecio(
            @Param("categoriaId") Integer categoriaId,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.destacado = true AND p.vendido = false ORDER BY p.fechaPublicacion DESC")
    List<Producto> findDestacados(Pageable pageable);

    @Query(value = "SELECT p.* FROM productos p " +
            "LEFT JOIN (SELECT producto_id, COUNT(*) as total FROM favoritos GROUP BY producto_id) f " +
            "ON p.id = f.producto_id " +
            "WHERE p.vendido = false " +
            "ORDER BY f.total DESC NULLS LAST, p.visitas DESC LIMIT 10", nativeQuery = true)
    List<Producto> findMasPopulares();

    @Query("SELECT p FROM Producto p WHERE p.vendido = false ORDER BY p.fechaPublicacion DESC")
    Page<Producto> findRecientes(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.vendido = false AND " +
            "(p.titulo LIKE %:termino% OR p.descripcion LIKE %:termino%)")
    Page<Producto> buscarPorCategoriaYTermino(@Param("categoriaId") Integer categoriaId,
                                              @Param("termino") String termino,
                                              Pageable pageable);

    // Método para búsqueda combinada (categoría, término y rango de precio)
    @Query("SELECT p FROM Producto p WHERE p.categoria.id = :categoriaId AND p.vendido = false AND " +
            "(p.titulo LIKE %:termino% OR p.descripcion LIKE %:termino%) AND " +
            "p.precio BETWEEN :precioMin AND :precioMax")
    Page<Producto> buscarPorCategoriaTerminoYPrecio(@Param("categoriaId") Integer categoriaId,
                                                    @Param("termino") String termino,
                                                    @Param("precioMin") BigDecimal precioMin,
                                                    @Param("precioMax") BigDecimal precioMax,
                                                    Pageable pageable);

}