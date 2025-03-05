package com.segundito.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Integer id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private BigDecimal precio;

    @NotNull(message = "La categoría es obligatoria")
    private Integer categoriaId;

    @NotNull(message = "El estado del producto es obligatorio")
    private Integer estadoId;

    @NotNull(message = "La ubicación es obligatoria")
    private Integer ubicacionId;

    private Boolean destacado = false;

    private Boolean vendido = false;
}