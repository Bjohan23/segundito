package com.segundito.app.controller.web;

import com.segundito.app.dto.RegistroRequest;
import com.segundito.app.dto.UsuarioDTO;
import com.segundito.app.entity.Usuario;
import com.segundito.app.service.UsuarioService;
import com.segundito.app.util.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.UUID;

@Controller
public class AuthViewController {

    private final UsuarioService usuarioService;
    private final FileUploadUtil fileUploadUtil;

    @Autowired
    public AuthViewController(UsuarioService usuarioService , FileUploadUtil fileUploadUtil) {
        this.usuarioService = usuarioService;
        this.fileUploadUtil = fileUploadUtil;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Iniciar sesión - Segundito");
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("title", "Registro - Segundito");
        model.addAttribute("registroRequest", new RegistroRequest());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("registroRequest") RegistroRequest registroRequest,
                                   BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "registro";
        }

        // Verificar si el email ya está registrado
        if (usuarioService.existeEmail(registroRequest.getEmail())) {
            result.rejectValue("email", "email.duplicate", "El email ya está registrado");
            return "registro";
        }

        try {
            // Crear el usuario
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setNombre(registroRequest.getNombre());
            usuarioDTO.setEmail(registroRequest.getEmail());
            usuarioDTO.setPassword(registroRequest.getPassword());
            usuarioDTO.setTelefono(registroRequest.getTelefono());
            usuarioDTO.setBiografia(registroRequest.getBiografia());
            usuarioDTO.setRolId(2); // ROLE_USER por defecto

            // Procesar foto de perfil
            MultipartFile fotoPerfilFile = registroRequest.getFotoPerfil();
            if (fotoPerfilFile != null && !fotoPerfilFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + fotoPerfilFile.getOriginalFilename();
                String rutaFotoPerfil = fileUploadUtil.saveFile("usuarios", fileName, fotoPerfilFile);
                usuarioDTO.setFotoPerfil(rutaFotoPerfil);
            }

            Usuario usuario = usuarioService.guardar(usuarioDTO);

            redirectAttributes.addFlashAttribute("alertSuccess", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";

        } catch (IOException e) {
            result.reject("error.global", "Error al procesar la imagen: " + e.getMessage());
            return "registro";
        } catch (Exception e) {
            result.reject("error.global", "Error en el registro: " + e.getMessage());
            return "registro";
        }
    }
}