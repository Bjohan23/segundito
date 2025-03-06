package com.segundito.app.controller.web;

import com.segundito.app.dto.ProductoResponseDTO;
import com.segundito.app.dto.UsuarioDTO;
import com.segundito.app.entity.Usuario;
import com.segundito.app.service.CategoriaService;
import com.segundito.app.service.ProductoService;
import com.segundito.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PerfilController {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @Autowired
    public PerfilController(UsuarioService usuarioService,
                            ProductoService productoService,
                            CategoriaService categoriaService) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            try {
                Usuario usuario = usuarioService.buscarPorEmail(email);
                model.addAttribute("usuario", usuario);

                // Obtener productos del usuario
                Page<ProductoResponseDTO> productosPage = productoService.listarPorUsuario(
                        usuario.getId(), PageRequest.of(0, 10));
                List<ProductoResponseDTO> productos = productosPage.getContent();
                model.addAttribute("productos", productos);

                // Filtrar productos activos, vendidos y destacados
                List<ProductoResponseDTO> productosActivos = productos.stream()
                        .filter(p -> !p.getVendido())
                        .collect(Collectors.toList());
                List<ProductoResponseDTO> productosVendidos = productos.stream()
                        .filter(ProductoResponseDTO::getVendido)
                        .collect(Collectors.toList());
                List<ProductoResponseDTO> productosDestacados = productos.stream()
                        .filter(ProductoResponseDTO::getDestacado)
                        .collect(Collectors.toList());

                model.addAttribute("productosActivos", productosActivos);
                model.addAttribute("productosVendidos", productosVendidos);
                model.addAttribute("productosDestacados", productosDestacados);

                // Valores vacíos para las otras secciones para evitar errores
                model.addAttribute("favoritos", List.of());
                model.addAttribute("mensajesNoLeidos", 0);
                model.addAttribute("valoraciones", List.of());
                model.addAttribute("puntuacionMedia", 0.0);
                model.addAttribute("totalValoraciones", 0L);
                model.addAttribute("categorias", categoriaService.listarTodas());

                model.addAttribute("title", "Mi perfil - Segundito");
                return "perfil";
            } catch (Exception e) {
                model.addAttribute("alertError", "Error al cargar el perfil: " + e.getMessage());
                return "redirect:/";
            }
        } else {
            return "redirect:/login?error=nologin";
        }
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam("nombre") String nombre,
            @RequestParam("telefono") String telefono,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "biografia", required = false) String biografia,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());

                // Crear DTO con los datos actualizados
                UsuarioDTO usuarioDTO = new UsuarioDTO();
                usuarioDTO.setNombre(nombre);
                usuarioDTO.setEmail(usuario.getEmail());
                usuarioDTO.setTelefono(telefono);
                usuarioDTO.setBiografia(biografia);

                // Solo actualizar contraseña si se proporciona
                if (password != null && !password.isEmpty()) {
                    usuarioDTO.setPassword(password);
                }

                // Actualizar usuario
                usuarioService.actualizar(usuario.getId(), usuarioDTO);

                // Actualizar foto de perfil si se proporciona
                if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                    String ruta = guardarImagen(fotoPerfil, "perfiles", usuario.getId().toString());
                    usuarioService.actualizarFotoPerfil(usuario.getId(), ruta);
                }

                redirectAttributes.addFlashAttribute("alertSuccess", "Perfil actualizado correctamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("alertError", "Error al actualizar el perfil: " + e.getMessage());
            }

            return "redirect:/perfil";
        } else {
            return "redirect:/login?error=nologin";
        }
    }

    // Método auxiliar para guardar imágenes (simplificado)
    private String guardarImagen(MultipartFile archivo, String carpeta, String identificador) {
        try {
            // Aquí deberías implementar la lógica para guardar el archivo
            // Por ahora, devolvemos una ruta ficticia
            return carpeta + "/" + identificador + "/" + archivo.getOriginalFilename();
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }
    }
}