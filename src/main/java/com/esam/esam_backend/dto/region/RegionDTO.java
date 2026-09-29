package com.esam.esam_backend.dto.region;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
public class RegionDTO {

    @Min(value = 1)
    private Long idRegion;
    
    @NotBlank 
    @Size(max = 25) 
    private String nombre;
}
