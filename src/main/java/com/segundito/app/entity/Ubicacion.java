package com.segundito.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "ubicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "La provincia es obligatoria")
    @Column(nullable = false, length = 100)
    private String provincia;

    @NotBlank(message = "La ciudad es obligatoria")
    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    @Column
    private Boolean activa = true;

    @OneToMany(mappedBy = "ubicacion")
    private Set<Producto> productos;
}