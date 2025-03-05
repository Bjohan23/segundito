package com.segundito.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "valoraciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(nullable = false)
    private Integer calificacion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @CreationTimestamp
    @Column(name = "fecha_valoracion", updatable = false)
    private LocalDateTime fechaValoracion;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}