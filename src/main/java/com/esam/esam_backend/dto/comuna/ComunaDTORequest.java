package com.esam.esam_backend.dto.comuna;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
public class ComunaDTORequest {

    @NotBlank 
    @Size(max = 25)
    private String nombre;
    
    @NotNull
    @Min(value = 1) 
    private Long idRegion;
}
