package com.segundito.app.controller.web;

import com.segundito.app.dto.RegistroRequest;
import com.segundito.app.dto.UsuarioDTO;
import com.segundito.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthViewController {

    private final UsuarioService usuarioService;

    @Autowired
    public AuthViewController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Registro - Segundito");
            return "registro";
        }

        try {
            // Convertir RegistroRequest a UsuarioDTO si es necesario
            // Ejemplo:
             UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setNombre(registroRequest.getNombre());
            usuarioDTO.setEmail(registroRequest.getEmail());
            usuarioDTO.setPassword(registroRequest.getPassword());
            usuarioDTO.setTelefono(registroRequest.getTelefono());
            usuarioDTO.setBiografia(registroRequest.getBiografia());
            usuarioDTO.setRolId(2); // ROLE_USER por defecto

            //Guarda el usuario
            usuarioService.guardar(usuarioDTO);

            // También puedes usar directamente el registroRequest si tu servicio lo acepta
            //usuarioService.registrarUsuario(registroRequest);

            redirectAttributes.addFlashAttribute("alertSuccess", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("title", "Registro - Segundito");
            model.addAttribute("alertError", "Error al registrar: " + e.getMessage());
            return "registro";
        }
    }
}