package com.segundito.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String tipo = "Bearer";
    private Integer id;
    private String email;
    private String nombre;
    private String rol;

    public JwtResponse(String token, Integer id, String email, String nombre, String rol) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }
}