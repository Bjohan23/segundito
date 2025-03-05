package com.segundito.app.service;

import com.segundito.app.dto.MensajeDTO;
import com.segundito.app.dto.MensajeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MensajeService {

    MensajeResponseDTO enviar(MensajeDTO mensajeDTO, Integer emisorId);

    MensajeResponseDTO buscarPorId(Integer id, Integer usuarioId);

    List<MensajeResponseDTO> listarConversacion(Integer productoId, Integer usuarioId);

    Page<MensajeResponseDTO> listarNoLeidos(Integer usuarioId, Pageable pageable);

    Long contarNoLeidos(Integer usuarioId);

    Map<Integer, String> listarConversacionesActivas(Integer usuarioId);

    void marcarComoLeidos(Integer productoId, Integer usuarioId);
}