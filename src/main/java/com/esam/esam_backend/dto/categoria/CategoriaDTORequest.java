package com.esam.esam_backend.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoriaDTORequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    private Long idCategoriaPadre;
}