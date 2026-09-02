package com.esam.esam_backend.dto.producto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoDTOResponse {

    private Long sku;
    private String nombre;
    private String descripcion;
    private String marca;
    private Long precio;
    private Long stock;
    private String img;
}
