package com.segundito.app.controller.web;

import org.springframework.web.bind.annotation.PathVariable;
import com.segundito.app.dto.ProductoDTO;
import com.segundito.app.service.CategoriaService;
import com.segundito.app.service.EstadoProductoService;
import com.segundito.app.service.ProductoService;
import com.segundito.app.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/productos")
public class ProductoViewController {

    private final CategoriaService categoriaService;
    private final EstadoProductoService estadoProductoService;
    private final UbicacionService ubicacionService;
    private final ProductoService productoService;

    @Autowired
    public ProductoViewController(CategoriaService categoriaService,
                                  EstadoProductoService estadoProductoService,
                                  UbicacionService ubicacionService,
                                  ProductoService productoService) {
        this.categoriaService = categoriaService;
        this.estadoProductoService = estadoProductoService;
        this.ubicacionService = ubicacionService;
        this.productoService = productoService;
    }

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        model.addAttribute("title", "Publicar producto - Segundito");
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("estados", estadoProductoService.listarTodos());
        model.addAttribute("ubicaciones", ubicacionService.listarTodas());
        model.addAttribute("productoDTO", new ProductoDTO());
        return "productos/nuevo";
    }

    @GetMapping("/busqueda")
    public String buscarProductos(Model model) {
        model.addAttribute("title", "Buscar productos - Segundito");
        return "productos/busqueda";
    }

    @GetMapping("/detalle/{id}")
    public String detalleProducto(@PathVariable Integer id, Model model) {
        model.addAttribute("title", "Detalle de producto - Segundito");
        model.addAttribute("producto", productoService.buscarPorId(id));
        return "productos/detalle";
    }
}