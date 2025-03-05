package com.segundito.app.controller;

import com.segundito.app.dto.MensajeDTO;
import com.segundito.app.dto.MensajeResponseDTO;
import com.segundito.app.service.MensajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    private final MensajeService mensajeService;

    @Autowired
    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping
    public ResponseEntity<MensajeResponseDTO> enviarMensaje(
            @Valid @RequestBody MensajeDTO mensajeDTO,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer emisorId = 1; // TODO: Obtener de autenticación

        MensajeResponseDTO nuevoMensaje = mensajeService.enviar(mensajeDTO, emisorId);
        return new ResponseEntity<>(nuevoMensaje, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponseDTO> obtenerMensaje(
            @PathVariable Integer id,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        MensajeResponseDTO mensaje = mensajeService.buscarPorId(id, usuarioId);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MensajeResponseDTO>> listarConversacion(
            @PathVariable Integer productoId,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        List<MensajeResponseDTO> conversacion = mensajeService.listarConversacion(productoId, usuarioId);
        return ResponseEntity.ok(conversacion);
    }

    @GetMapping("/no-leidos")
    public ResponseEntity<Page<MensajeResponseDTO>> listarNoLeidos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaEnvio").descending());
        Page<MensajeResponseDTO> mensajes = mensajeService.listarNoLeidos(usuarioId, pageable);
        return ResponseEntity.ok(mensajes);
    }

    @GetMapping("/contar-no-leidos")
    public ResponseEntity<Map<String, Long>> contarNoLeidos(Principal principal) {
        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        Long cantidad = mensajeService.contarNoLeidos(usuarioId);
        return ResponseEntity.ok(Map.of("cantidad", cantidad));
    }

    @GetMapping("/conversaciones")
    public ResponseEntity<Map<Integer, String>> listarConversacionesActivas(Principal principal) {
        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        Map<Integer, String> conversaciones = mensajeService.listarConversacionesActivas(usuarioId);
        return ResponseEntity.ok(conversaciones);
    }

    @PatchMapping("/producto/{productoId}/leidos")
    public ResponseEntity<Void> marcarComoLeidos(
            @PathVariable Integer productoId,
            Principal principal) {

        // En un entorno real, obtendríamos el ID del usuario autenticado
        Integer usuarioId = 1; // TODO: Obtener de autenticación

        mensajeService.marcarComoLeidos(productoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}