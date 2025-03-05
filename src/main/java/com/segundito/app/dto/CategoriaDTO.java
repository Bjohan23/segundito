package com.segundito.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    private String icono;

    private Integer categoriaPadreId;

    private Boolean activa = true;
}