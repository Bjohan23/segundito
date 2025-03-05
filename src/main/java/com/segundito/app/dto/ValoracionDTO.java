package com.segundito.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionDTO {

    private Integer id;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    private String comentario;

    @NotNull(message = "El ID del producto es obligatorio")
    private Integer productoId;

    @NotNull(message = "El ID del vendedor es obligatorio")
    private Integer vendedorId;
}