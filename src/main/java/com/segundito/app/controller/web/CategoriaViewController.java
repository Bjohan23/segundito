package com.segundito.app.controller.web;

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
        model.addAttribute("title", "Categorías - Segundito");
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "categoria/listar";
    }

    @GetMapping("/{id}")
    public String verCategoria(
            @PathVariable Integer id,
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
            model.addAttribute("categoria", categoriaService.buscarPorId(id));
            model.addAttribute("subcategorias", categoriaService.listarSubcategorias(id));

            // Obtener productos de la categoría
            Page<ProductoResponseDTO> productosPage = productoService.listarPorCategoria(id, pageable);

            // Añadir información de productos al modelo
            model.addAttribute("productos", productosPage.getContent());
            model.addAttribute("totalElementos", productosPage.getTotalElements());
            model.addAttribute("totalPaginas", productosPage.getTotalPages());
            model.addAttribute("paginaActual", productosPage.getNumber());
            model.addAttribute("tamanoPagina", productosPage.getSize());

            // Depuración
            System.out.println("Productos encontrados para categoría " + id + ": " + productosPage.getTotalElements());

            model.addAttribute("title", "Categoría - Segundito");
            return "categoria/listar";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar la categoría: " + e.getMessage());
            return "error/404";
        }
    }
}