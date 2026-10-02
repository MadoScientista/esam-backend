package com.esam.esam_backend.dto.producto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoDTORequest {

    @NotBlank
    @Size(max = 50)
    private String sku;

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @Size(max = 5000)
    private String descripcion;

    @NotNull
    @Positive
    private Long precio;

    @NotNull
    @PositiveOrZero
    private Integer stock;

    @NotNull
    private Long idMarca;

    @NotEmpty
    private Set<Long> idCategorias;
}
