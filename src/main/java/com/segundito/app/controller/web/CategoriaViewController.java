package com.segundito.app.controller.web;

import com.segundito.app.service.CategoriaService;
import com.segundito.app.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String verCategoria(@PathVariable Integer id, Model model) {
        model.addAttribute("title", "Categoría - Segundito");
        model.addAttribute("categoria", categoriaService.buscarPorId(id));
        model.addAttribute("subcategorias", categoriaService.listarSubcategorias(id));
        return "categoria/listar";
    }
}