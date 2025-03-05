package com.segundito.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "producto_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @CreationTimestamp
    @Column(name = "fecha_agregado", updatable = false)
    private LocalDateTime fechaAgregado;
}