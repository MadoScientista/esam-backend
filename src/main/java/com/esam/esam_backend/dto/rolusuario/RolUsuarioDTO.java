package com.esam.esam_backend.dto.rolusuario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RolUsuarioDTO {

    @Min(value = 1)
    private Long idRolUsuario;

    @NotBlank 
    @Size(max=25)
    private String nombre;
}
