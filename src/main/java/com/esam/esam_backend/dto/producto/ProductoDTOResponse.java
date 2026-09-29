package com.esam.esam_backend.dto.producto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoDTOResponse {

    @Min(value = 1)
    private Long sku;

    @NotBlank 
    @Size(max=50)
    private String nombre;
    
    @NotBlank
    private String descripcion;
    
    @NotBlank 
    private String marca;
    
    @NotNull 
    @Min(value = 0)
    private Long precio;
    
    @NotNull 
    @Min(value = 0)
    private Long stock;
    
    private String img;
}
