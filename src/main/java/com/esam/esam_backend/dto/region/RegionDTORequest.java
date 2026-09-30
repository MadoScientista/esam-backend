package com.esam.esam_backend.dto.region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegionDTORequest {

    @NotBlank
    @Size(max = 50)
    private String nombre;
}
