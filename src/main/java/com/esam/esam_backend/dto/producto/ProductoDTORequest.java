package com.esam.esam_backend.dto.producto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoDTORequest {

    private String nombre;
    private String descripcion;
    private Long idMarca;
    private Long precio;
    private Long stock;
    private String img;
}
