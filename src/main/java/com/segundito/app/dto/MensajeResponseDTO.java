package com.segundito.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponseDTO {

    private Integer id;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean leido;

    private Integer emisorId;
    private String emisorNombre;
    private String emisorEmail;

    private Integer receptorId;
    private String receptorNombre;
    private String receptorEmail;

    private Integer productoId;
    private String productoTitulo;
    private String productoImagen;
}