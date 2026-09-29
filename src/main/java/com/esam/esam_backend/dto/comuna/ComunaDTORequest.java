package com.esam.esam_backend.dto.comuna;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
public class ComunaDTORequest {

    @NotBlank 
    private String nombre;
    
    @Min(value = 1) 
    private Long idRegion;
}
