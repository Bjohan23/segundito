package com.segundito.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column
    private String descripcion;

    @Column(length = 100)
    private String icono;

    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL)
    private Set<Categoria> subcategorias;

    @Column
    private Boolean activa = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "categoria")
    private Set<Producto> productos;
}