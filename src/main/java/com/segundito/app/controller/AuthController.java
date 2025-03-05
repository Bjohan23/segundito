package com.segundito.app.controller;

import com.segundito.app.dto.JwtResponse;
import com.segundito.app.dto.LoginRequest;
import com.segundito.app.dto.RegistroRequest;
import com.segundito.app.dto.UsuarioDTO;
import com.segundito.app.entity.Usuario;
import com.segundito.app.security.JwtUtil;
import com.segundito.app.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioService usuarioService,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> autenticarUsuario(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);

        // Obtener datos del usuario
        Usuario usuario = usuarioService.buscarPorEmail(loginRequest.getEmail());

        // Actualizar último acceso
        usuarioService.actualizarUltimoAcceso(usuario.getId());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRol().getNombre()
        ));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequest registroRequest) {
        // Verificar si el email ya está registrado
        if (usuarioService.existeEmail(registroRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: El email ya está en uso");
        }

        // Crear el usuario
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombre(registroRequest.getNombre());
        usuarioDTO.setEmail(registroRequest.getEmail());
        usuarioDTO.setPassword(registroRequest.getPassword());
        usuarioDTO.setTelefono(registroRequest.getTelefono());
        usuarioDTO.setBiografia(registroRequest.getBiografia());
        usuarioDTO.setRolId(2); // ROLE_USER por defecto

        Usuario usuario = usuarioService.guardar(usuarioDTO);

        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registroRequest.getEmail(),
                        registroRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getRol().getNombre()
        ));
    }
}