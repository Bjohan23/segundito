package com.segundito.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @CreationTimestamp
    @Column(name = "fecha_publicacion", updatable = false)
    private LocalDateTime fechaPublicacion;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoProducto estado;

    @ManyToOne
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;

    @Column
    private Boolean destacado = false;

    @Column
    private Boolean vendido = false;

    @Column
    private Integer visitas = 0;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Imagen> imagenes;

    @OneToMany(mappedBy = "producto")
    private Set<Valoracion> valoraciones;

    @OneToMany(mappedBy = "producto")
    private Set<Mensaje> mensajes;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorito> favoritos;
}