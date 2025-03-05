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
@Table(name = "historial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La acción es obligatoria")
    @Column(nullable = false, length = 100)
    private String accion;

    @Column
    private String descripcion;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @NotBlank(message = "La entidad es obligatoria")
    @Column(nullable = false, length = 50)
    private String entidad;

    @NotNull(message = "El ID de la entidad es obligatorio")
    @Column(name = "entidad_id", nullable = false)
    private Integer entidadId;
}