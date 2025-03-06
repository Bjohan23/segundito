package com.segundito.app.controller.web;

import com.segundito.app.dto.ProductoResponseDTO;
import com.segundito.app.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ProductoService productoService;

    @Autowired
    public HomeController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        try {
            // Cargar productos destacados (8 como máximo)
            List<ProductoResponseDTO> productosDestacados = productoService.listarDestacados(8);
            model.addAttribute("productos", productosDestacados);

            // Cargar productos recientes (8 como máximo)
            Pageable pageable = PageRequest.of(0, 8);
            List<ProductoResponseDTO> productosRecientes = productoService.listarRecientes(pageable).getContent();
            model.addAttribute("productosRecientes", productosRecientes);

            model.addAttribute("title", "Segundito - Marketplace de segunda mano");
            return "index";
        } catch (Exception e) {
            // Registrar el error
            System.err.println("Error al cargar datos para la página principal: " + e.getMessage());
            e.printStackTrace();

            // Aún así, mostrar la página pero sin productos
            model.addAttribute("title", "Segundito - Marketplace de segunda mano");
            model.addAttribute("errorCarga", "No se pudieron cargar los productos. Por favor, inténtelo de nuevo más tarde.");
            return "index";
        }
    }
}