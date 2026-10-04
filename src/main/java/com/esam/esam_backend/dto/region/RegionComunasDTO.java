package com.esam.esam_backend.dto.region;

import java.util.List;

import com.esam.esam_backend.dto.comuna.ComunaDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegionComunasDTO {

    @Min(value = 1)
    private Long idRegion;
    
    @NotBlank
    @Size(max = 50)
    private String nombre;
    
    private List<ComunaDTO> comunas;

}
