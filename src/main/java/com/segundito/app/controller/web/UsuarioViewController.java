package com.segundito.app.controller.web;

import com.segundito.app.dto.ProductoResponseDTO;
import com.segundito.app.entity.Usuario;
import com.segundito.app.service.ProductoService;
import com.segundito.app.service.UsuarioService;
import com.segundito.app.service.ValoracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final ValoracionService valoracionService;

    @Autowired
    public UsuarioViewController(UsuarioService usuarioService,
                                 ProductoService productoService,
                                 ValoracionService valoracionService) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.valoracionService = valoracionService;
    }

    @GetMapping("/{id}")
    public String perfilUsuario(@PathVariable Integer id, Model model) {
        try {
            // Obtener información del usuario
            Usuario usuario = usuarioService.buscarPorId(id);
            model.addAttribute("usuario", usuario);

            // Obtener los productos publicados por este usuario
            Pageable pageable = PageRequest.of(0, 8);
            Page<ProductoResponseDTO> productosUsuario = productoService.listarPorUsuario(id, pageable);
            model.addAttribute("productos", productosUsuario.getContent());

            // Obtener calificación media y total de valoraciones
            Double calificacionMedia = valoracionService.obtenerCalificacionPromedio(id);
            Long totalValoraciones = valoracionService.contarValoraciones(id);
            model.addAttribute("calificacionMedia", calificacionMedia);
            model.addAttribute("totalValoraciones", totalValoraciones);

            model.addAttribute("title", usuario.getNombre() + " - Perfil en Segundito");

            return "usuarios/perfil";
        } catch (Exception e) {
            System.err.println("Error al cargar perfil de usuario: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "No se pudo cargar el perfil del usuario solicitado.");
            return "error/404";
        }
    }
}