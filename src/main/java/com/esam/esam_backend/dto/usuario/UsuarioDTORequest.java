package com.esam.esam_backend.dto.usuario;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTORequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    private String apellido;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    private String telefono;

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

    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    @NotNull
    @Min(value = 1)
    private Long idRegion;

    @NotNull
    @Min(value = 1)
    private Long idComuna;

    @Min(value = 1)
    private Long idDireccion;
}