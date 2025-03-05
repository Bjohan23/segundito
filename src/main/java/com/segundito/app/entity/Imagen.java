package com.segundito.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "imagenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Column(nullable = false)
    private String url;

    @Column(name = "es_principal")
    private Boolean esPrincipal = false;

    @NotNull(message = "El orden es obligatorio")
    @Column(nullable = false)
    private Integer orden;

    @CreationTimestamp
    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime fechaSubida;
}