package com.esam.esam_backend.dto.imagenProducto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImagenProductoResponse {

    @Min(value = 1)
    private Long idImagenProducto;

    @NotBlank
    private String url;

    private Integer orden;
}
