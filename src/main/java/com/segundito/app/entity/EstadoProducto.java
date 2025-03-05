package com.segundito.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "estados_producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 50)
    private String nombre;

    @Column
    private String descripcion;

    @OneToMany(mappedBy = "estado")
    private Set<Producto> productos;
}