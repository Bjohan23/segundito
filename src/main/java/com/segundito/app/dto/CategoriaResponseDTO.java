package com.segundito.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Integer categoriaPadreId;
    private String categoriaPadreNombre;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private List<CategoriaResponseDTO> subcategorias;
    private Long cantidadProductos;
}