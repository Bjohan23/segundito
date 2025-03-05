package com.segundito.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @CreationTimestamp
    @Column(name = "fecha_envio", updatable = false)
    private LocalDateTime fechaEnvio;

    @Column
    private Boolean leido = false;

    @ManyToOne
    @JoinColumn(name = "emisor_id", nullable = false)
    private Usuario emisor;

    @ManyToOne
    @JoinColumn(name = "receptor_id", nullable = false)
    private Usuario receptor;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}