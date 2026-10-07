package com.esam.esam_backend.dto.producto;

import com.esam.esam_backend.dto.imagenProducto.ImagenProductoResponse;
import com.esam.esam_backend.dto.marca.MarcaDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoResumenDTOResponse {

    private Long idProducto;
    private String nombre;
    private Long precio;
    private ImagenProductoResponse imagenPrincipal;
    private MarcaDTO marca;
    private Integer stock;
    private Integer stockReservado;
}
