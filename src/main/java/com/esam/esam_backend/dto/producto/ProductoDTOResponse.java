package com.esam.esam_backend.dto.producto;

import java.util.List;

import com.esam.esam_backend.dto.imagenProducto.ImagenProductoResponse;
import com.esam.esam_backend.dto.marca.MarcaDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoDTOResponse {

    @Min(value = 1)
    private Long idProducto;

    @NotBlank
    @Size(max = 50)
    private String sku;

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @Size(max = 5000)
    private String descripcion;

    @NotNull
    @Min(value = 0)
    private Long precio;

    @NotNull
    @Min(value = 0)
    private Integer stock;

    @NotBlank
    private String marca;

    private MarcaDTO marcaDetalle;

    private List<ImagenProductoResponse> imagenes;

    private Long idMarca;
}
