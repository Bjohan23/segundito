package com.segundito.app.controller.web;

import com.segundito.app.dto.CategoriaResponseDTO;
import com.segundito.app.dto.ProductoResponseDTO;
import com.segundito.app.service.CategoriaService;
import com.segundito.app.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/categorias")
public class CategoriaViewController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    @Autowired
    public CategoriaViewController(CategoriaService categoriaService, ProductoService productoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listarCategorias(Model model) {
        List<CategoriaResponseDTO> categoriasPrincipales = categoriaService.listarPrincipales();
        model.addAttribute("categoriasPrincipales", categoriasPrincipales);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("title", "Categorías - Segundito");
        return "categorias/listar";
    }

    @GetMapping("/{id}")
    public String verCategoria(
            @PathVariable Integer id,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer subcategoriaId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(defaultValue = "fechaPublicacion") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {
        try {
            // Configuración de ordenación
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

            // Obtener categoría
            CategoriaResponseDTO categoria = categoriaService.buscarPorId(id);
            model.addAttribute("categoria", categoria);
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("subcategorias", categoriaService.listarSubcategorias(id));

            // ID de categoría a usar (puede ser subcategoría si se especifica)
            Integer categoriaIdAUsar = subcategoriaId != null ? subcategoriaId : id;

            // Obtener productos de la categoría con filtros
            Page<ProductoResponseDTO> productosPage;

            // Valores por defecto para precios si no se especifican
            BigDecimal minPrecio = precioMin != null ? precioMin : BigDecimal.ZERO;
            BigDecimal maxPrecio = precioMax != null ? precioMax : new BigDecimal("999999999");

            if (termino != null && !termino.isEmpty() && (precioMin != null || precioMax != null)) {
                // Búsqueda combinada: término + categoría + rango precio
                productosPage = productoService.buscarPorCategoriaTerminoYPrecio(
                        categoriaIdAUsar, termino, minPrecio, maxPrecio, pageable);
            } else if (termino != null && !termino.isEmpty()) {
                // Búsqueda por término + categoría
                productosPage = productoService.buscarPorTerminoYCategoria(termino, categoriaIdAUsar, pageable);
            } else if (precioMin != null || precioMax != null) {
                // Búsqueda por rango de precio + categoría
                productosPage = productoService.buscarPorCategoriaYRangoPrecio(
                        categoriaIdAUsar, minPrecio, maxPrecio, pageable);
            } else {
                // Búsqueda estándar por categoría
                productosPage = productoService.listarPorCategoria(categoriaIdAUsar, pageable);
            }

            // Añadir información de productos al modelo
            model.addAttribute("productos", productosPage.getContent());
            model.addAttribute("totalElementos", productosPage.getTotalElements());
            model.addAttribute("totalPaginas", productosPage.getTotalPages());
            model.addAttribute("paginaActual", productosPage.getNumber());
            model.addAttribute("tamanoPagina", productosPage.getSize());

            model.addAttribute("title", categoria.getNombre() + " - Segundito");
            return "categorias/detalle";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar la categoría: " + e.getMessage());
            return "error/404";
        }
    }
}