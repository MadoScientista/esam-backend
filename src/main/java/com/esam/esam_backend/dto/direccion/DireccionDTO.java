package com.esam.esam_backend.dto.direccion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DireccionDTO {

    @NotNull
    @Min(value = 1)
    private Long idDireccion;

    @NotBlank
    @Size(max = 150)
    private String nombreReceptor;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    private String telefonoReceptor;

    @NotBlank
    @Size(max = 150)
    private String calle;

    @NotBlank
    @Size(max = 20)
    private String numero;

    @Size(max = 200)
    private String complemento;

    private Boolean predeterminada;
    private Boolean activo;

    @NotNull
    @Min(value = 1)
    private Long idUsuario;

    @NotNull
    @Min(value = 1)
    private Long idComuna;
}
