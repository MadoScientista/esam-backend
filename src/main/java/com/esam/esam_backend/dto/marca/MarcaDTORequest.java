package com.esam.esam_backend.dto.marca;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MarcaDTORequest {

    @NotBlank
    @Size(max = 25)
    private String nombre;
}
