package com.esam.esam_backend.dto.producto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.esam.esam_backend.dto.productImage.ProductImageResponse;

@Data
@NoArgsConstructor
public class ProductoDTOResponse {

    @Min(value = 1)
    private Long sku;

    @NotBlank
    @Size(max=50)
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String marca;

    @NotNull
    @Min(value = 0)
    private Long precio;

    @NotNull
    @Min(value = 0)
    private Long stock;

    // Un producto puede tener muchas imágenes, la principal viene primero
    private List<ProductImageResponse> imagenes;
}
