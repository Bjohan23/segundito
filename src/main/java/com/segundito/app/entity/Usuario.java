package com.segundito.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column
    private String biografia;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column
    private Boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Set<Producto> productos;

    @OneToMany(mappedBy = "vendedor")
    private Set<Valoracion> valoracionesRecibidas;

    @OneToMany(mappedBy = "comprador")
    private Set<Valoracion> valoracionesEmitidas;

    @OneToMany(mappedBy = "emisor")
    private Set<Mensaje> mensajesEnviados;

    @OneToMany(mappedBy = "receptor")
    private Set<Mensaje> mensajesRecibidos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Set<Favorito> favoritos;
}