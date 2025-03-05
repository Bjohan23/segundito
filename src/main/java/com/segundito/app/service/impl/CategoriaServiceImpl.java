package com.segundito.app.service.impl;

import com.segundito.app.dto.CategoriaDTO;
import com.segundito.app.dto.CategoriaResponseDTO;
import com.segundito.app.entity.Categoria;
import com.segundito.app.exception.BadRequestException;
import com.segundito.app.exception.ResourceNotFoundException;
import com.segundito.app.repository.CategoriaRepository;
import com.segundito.app.repository.ProductoRepository;
import com.segundito.app.service.CategoriaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository,
                                ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public CategoriaResponseDTO guardar(CategoriaDTO categoriaDTO) {
        // Verificar si ya existe una categoría con el mismo nombre
        if (categoriaRepository.findByNombre(categoriaDTO.getNombre()).isPresent()) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setIcono(categoriaDTO.getIcono());
        categoria.setActiva(categoriaDTO.getActiva() != null ? categoriaDTO.getActiva() : true);
        categoria.setFechaCreacion(LocalDateTime.now());
        categoria.setFechaModificacion(LocalDateTime.now());

        // Asignar categoría padre si existe
        if (categoriaDTO.getCategoriaPadreId() != null) {
            Categoria categoriaPadre = categoriaRepository.findById(categoriaDTO.getCategoriaPadreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría padre no encontrada con ID: " + categoriaDTO.getCategoriaPadreId()));
            categoria.setCategoriaPadre(categoriaPadre);
        }

        Categoria guardada = categoriaRepository.save(categoria);

        return convertirADTO(guardada);
    }

    @Override
    @Transactional
    public CategoriaResponseDTO actualizar(Integer id, CategoriaDTO categoriaDTO) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Verificar que no exista otra categoría con el mismo nombre (excepto la actual)
        categoriaRepository.findByNombre(categoriaDTO.getNombre())
                .ifPresent(cat -> {
                    if (!cat.getId().equals(id)) {
                        throw new BadRequestException("Ya existe otra categoría con ese nombre");
                    }
                });

        categoria.setNombre(categoriaDTO.getNombre());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setIcono(categoriaDTO.getIcono());

        if (categoriaDTO.getActiva() != null) {
            categoria.setActiva(categoriaDTO.getActiva());
        }

        categoria.setFechaModificacion(LocalDateTime.now());

        // Verificar que no genere un ciclo al cambiar la categoría padre
        if (categoriaDTO.getCategoriaPadreId() != null) {
            // No puede ser su propia categoría padre
            if (categoriaDTO.getCategoriaPadreId().equals(id)) {
                throw new BadRequestException("Una categoría no puede ser su propia categoría padre");
            }

            // Verificar que no sea una de sus subcategorías (evitar ciclos)
            if (esDescendiente(categoria, categoriaDTO.getCategoriaPadreId())) {
                throw new BadRequestException("No se puede asignar como categoría padre a una de sus subcategorías");
            }

            Categoria categoriaPadre = categoriaRepository.findById(categoriaDTO.getCategoriaPadreId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría padre no encontrada con ID: " + categoriaDTO.getCategoriaPadreId()));

            categoria.setCategoriaPadre(categoriaPadre);
        } else {
            categoria.setCategoriaPadre(null);
        }

        Categoria actualizada = categoriaRepository.save(categoria);

        return convertirADTO(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        // Verificar que no tenga subcategorías
        List<Categoria> subcategorias = categoriaRepository.findByCategoriaPadreId(id);
        if (!subcategorias.isEmpty()) {
            throw new BadRequestException(
                    "No se puede eliminar la categoría porque tiene " + subcategorias.size() + " subcategorías");
        }

        // Verificar que no tenga productos asociados
        long productosAsociados = productoRepository.findByCategoriaId(id, null).getTotalElements();
        if (productosAsociados > 0) {
            throw new BadRequestException(
                    "No se puede eliminar la categoría porque tiene " + productosAsociados + " productos asociados");
        }

        categoriaRepository.deleteById(id);
    }

    @Override
    public CategoriaResponseDTO buscarPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        return convertirADTO(categoria);
    }

    @Override
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaResponseDTO> listarPrincipales() {
        return categoriaRepository.findByCategoriaPadreIsNull().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaResponseDTO> listarSubcategorias(Integer categoriaPadreId) {
        return categoriaRepository.findByCategoriaPadreId(categoriaPadreId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaResponseDTO> listarArbol() {
        // Obtener solo las categorías principales (sin categoría padre)
        List<Categoria> categoriasPrincipales = categoriaRepository.findByCategoriaPadreIsNull();

        // Convertir a DTOs con subcategorías anidadas
        return categoriasPrincipales.stream()
                .map(this::convertirADTOConSubcategorias)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void activarDesactivar(Integer id, Boolean activa) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        categoria.setActiva(activa);
        categoria.setFechaModificacion(LocalDateTime.now());

        categoriaRepository.save(categoria);
    }

    // Método auxiliar para comprobar si una categoría es descendiente de otra
    private boolean esDescendiente(Categoria categoriaPotencialAncestro, Integer posibleDescendienteId) {
        // Obtener todas las subcategorías directas
        List<Categoria> subcategorias = categoriaRepository.findByCategoriaPadreId(categoriaPotencialAncestro.getId());

        // Verificar si alguna subcategoría es la que estamos buscando
        for (Categoria subcategoria : subcategorias) {
            if (subcategoria.getId().equals(posibleDescendienteId)) {
                return true;
            }

            // Verificar recursivamente las subcategorías de esta subcategoría
            if (esDescendiente(subcategoria, posibleDescendienteId)) {
                return true;
            }
        }

        return false;
    }

    // Método auxiliar para convertir Categoria a CategoriaResponseDTO (sin subcategorías)
    private CategoriaResponseDTO convertirADTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setIcono(categoria.getIcono());
        dto.setActiva(categoria.getActiva());
        dto.setFechaCreacion(categoria.getFechaCreacion());
        dto.setFechaModificacion(categoria.getFechaModificacion());

        // Información de categoría padre si existe
        if (categoria.getCategoriaPadre() != null) {
            dto.setCategoriaPadreId(categoria.getCategoriaPadre().getId());
            dto.setCategoriaPadreNombre(categoria.getCategoriaPadre().getNombre());
        }

        // Cantidad de productos en esta categoría
        dto.setCantidadProductos(productoRepository.findByCategoriaId(categoria.getId(), null).getTotalElements());

        return dto;
    }

    // Método para convertir a DTO incluyendo subcategorías de forma recursiva
    private CategoriaResponseDTO convertirADTOConSubcategorias(Categoria categoria) {
        CategoriaResponseDTO dto = convertirADTO(categoria);

        // Obtener subcategorías
        List<Categoria> subcategorias = categoriaRepository.findByCategoriaPadreId(categoria.getId());

        // Convertir subcategorías recursivamente
        List<CategoriaResponseDTO> subcategoriasDTO = subcategorias.stream()
                .map(this::convertirADTOConSubcategorias)
                .collect(Collectors.toList());

        dto.setSubcategorias(subcategoriasDTO);

        return dto;
    }
}