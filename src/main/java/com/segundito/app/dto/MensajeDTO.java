package com.segundito.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {

    private Integer id;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    private String contenido;

    @NotNull(message = "El ID del receptor es obligatorio")
    private Integer receptorId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Integer productoId;
}