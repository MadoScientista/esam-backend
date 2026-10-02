package com.esam.esam_backend.dto.usuario;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTORequest {

    @NotBlank
    @Size(max = 255)
    private String nombres;

    @NotBlank
    @Size(max = 255)
    private String aPaterno;

    @Size(max = 255)
    private String aMaterno;

    @NotNull
    @Min(value = 1)
    private Long rut;

    @NotBlank
    @Size(max = 1)
    private String dv;

    @NotNull
    private LocalDate fechaNacimiento;

    @Size(max = 300)
    private String direccion;

    private Long telefono;

    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    @NotBlank
    private String password;

    @NotNull
    @Min(value = 1)
    private Long idRegion;

    @NotNull
    @Min(value = 1)
    private Long idComuna;
}