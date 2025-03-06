package com.segundito.app.controller.web;

import com.segundito.app.dto.ProductoResponseDTO;
import com.segundito.app.dto.ImagenDTO;
import com.segundito.app.service.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.segundito.app.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoViewController {

    private final CategoriaService categoriaService;
    private final EstadoProductoService estadoProductoService;
    private final UbicacionService ubicacionService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    @Autowired
    public ProductoViewController(CategoriaService categoriaService,
                                  EstadoProductoService estadoProductoService,
                                  UbicacionService ubicacionService,
                                  ProductoService productoService ,
                                  UsuarioService usuarioService
                                  ) {
        this.categoriaService = categoriaService;
        this.estadoProductoService = estadoProductoService;
        this.ubicacionService = ubicacionService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
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
        try {
            ProductoResponseDTO producto = productoService.buscarPorId(id);

            // Depuración
            System.out.println("==================== DETALLE PRODUCTO ====================");
            System.out.println("ID: " + producto.getId());
            System.out.println("Título: " + producto.getTitulo());
            System.out.println("Imágenes: " + (producto.getImagenes() != null ? producto.getImagenes().size() : "null"));

            if (producto.getImagenes() != null && !producto.getImagenes().isEmpty()) {
                for (ImagenDTO img : producto.getImagenes()) {
                    System.out.println("  - ID: " + img.getId() + ", URL: " + img.getUrl() + ", Principal: " + img.getEsPrincipal());
                }
            }

            System.out.println("Imagen Principal: " + producto.getImagenPrincipal());
            System.out.println("==========================================================");

            model.addAttribute("producto", producto);
            model.addAttribute("title", producto.getTitulo() + " - Segundito");

            return "productos/detalle";
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el producto: " + e.getMessage());
            return "error/404";
        }
    }


    @PostMapping("/nuevo")
    public String guardarProducto(@Valid @ModelAttribute("productoDTO") ProductoDTO productoDTO,
                                  BindingResult result,
                                  @RequestParam("imagenes") List<MultipartFile> imagenes,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // Si hay errores de validación, regresamos al formulario
        if (result.hasErrors()) {
            model.addAttribute("title", "Publicar producto - Segundito");
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("estados", estadoProductoService.listarTodos());
            model.addAttribute("ubicaciones", ubicacionService.listarTodas());
            return "productos/nuevo";
        }

        try {
            // Obtener el ID del usuario actual (ajusta esto según tu implementación de seguridad)
            Integer usuarioId = obtenerUsuarioActualId();

            // Guardar el producto
            ProductoResponseDTO nuevoProducto = productoService.guardar(productoDTO, usuarioId);

            // Si hay imágenes, guardarlas
            if (!imagenes.isEmpty() && !imagenes.get(0).isEmpty()) {
                productoService.agregarImagenes(nuevoProducto.getId(), imagenes, usuarioId);
            }

            redirectAttributes.addFlashAttribute("alertSuccess", "Producto publicado correctamente");
            return "redirect:/productos/detalle/" + nuevoProducto.getId();
        } catch (Exception e) {
            // En caso de error, regresamos al formulario con el mensaje de error
            model.addAttribute("title", "Publicar producto - Segundito");
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("estados", estadoProductoService.listarTodos());
            model.addAttribute("ubicaciones", ubicacionService.listarTodas());
            model.addAttribute("alertError", "Error al publicar el producto: " + e.getMessage());
            return "productos/nuevo";
        }
    }

    // Método auxiliar para obtener el ID del usuario actual
    private Integer obtenerUsuarioActualId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar si hay autenticación y si el usuario no es anónimo
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal().toString())) {

            // Verificar qué tipo de Principal tenemos
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String email = userDetails.getUsername();

                try {
                    return usuarioService.buscarPorEmail(email).getId();
                } catch (Exception e) {
                    throw new RuntimeException("No se pudo encontrar el usuario con email: " + email);
                }
            } else if (authentication.getPrincipal() instanceof String) {
                String email = (String) authentication.getPrincipal();

                try {
                    return usuarioService.buscarPorEmail(email).getId();
                } catch (Exception e) {
                    throw new RuntimeException("No se pudo encontrar el usuario con email: " + email);
                }
            }
        }

        // Si llegamos aquí, no hay un usuario autenticado válido
        throw new RuntimeException("Debes iniciar sesión para realizar esta acción");
    }

    @GetMapping("/destacados")
    public String productosDestacados(Model model) {
        try {
            List<ProductoResponseDTO> productosDestacados = productoService.listarDestacados(8); // Mostrar 8 productos destacados
            model.addAttribute("productos", productosDestacados);
            model.addAttribute("title", "Productos destacados - Segundito");
            return "productos/destacados"; // Necesitarás crear esta plantilla
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar productos destacados: " + e.getMessage());
            return "error/500";
        }
    }

}