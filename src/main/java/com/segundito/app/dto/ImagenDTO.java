package com.segundito.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenDTO {

    private Integer id;
    private String url;
    private Boolean esPrincipal;
    private Integer orden;
}