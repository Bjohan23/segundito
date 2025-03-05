package com.segundito.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionResponseDTO {

    private Integer id;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaValoracion;

    private Integer compradorId;
    private String compradorNombre;
    private String compradorEmail;

    private Integer vendedorId;
    private String vendedorNombre;
    private String vendedorEmail;

    private Integer productoId;
    private String productoTitulo;
    private String productoImagen;
}