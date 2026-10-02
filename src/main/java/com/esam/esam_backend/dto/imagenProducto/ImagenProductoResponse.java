package com.esam.esam_backend.dto.imagenProducto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImagenProductoResponse {

    @Min(value = 1)
    private Long idImagenProducto;

    @NotBlank
    private String url;

    @Size(max = 200)
    private String textoAlternativo;

    private Integer orden;
    private Boolean principal;
}
