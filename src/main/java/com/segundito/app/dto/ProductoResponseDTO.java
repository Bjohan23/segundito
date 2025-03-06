package com.segundito.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {

    private Integer id;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaModificacion;

    private Integer usuarioId;
    private String usuarioNombre;
    private String usuarioTelefono;
    private String usuarioEmail;

    private Integer categoriaId;
    private String categoriaNombre;

    private Integer estadoId;
    private String estadoNombre;

    private Integer ubicacionId;
    private String ubicacionProvincia;
    private String ubicacionCiudad;

    private Boolean esFavorito = false;
    private Boolean destacado;
    private Boolean vendido;
    private Integer visitas;

    private List<ImagenDTO> imagenes;
    private String imagenPrincipal;

    private Double calificacionVendedor;
    private Long totalValoraciones;

    public Boolean getEsFavorito() {
        return esFavorito;
    }

    public void setEsFavorito(Boolean esFavorito) {
        this.esFavorito = esFavorito;
    }
}
