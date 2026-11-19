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

    public interface Creacion {}

    @NotBlank(groups = Creacion.class)
    @Pattern(regexp = ".*\\S.*")
    @Size(max = 100)
    private String nombre;

    @Size(max = 100)
    @NotBlank(groups = Creacion.class)
    @Pattern(regexp = ".*\\S.*")
    private String apellido;

    @Email
    @Size(max = 254)
    @NotBlank(groups = Creacion.class)
    @Pattern(regexp = ".*\\S.*")
    private String email;

    @Size(min = 8, max = 72)
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    private String telefono;

    @Size(max = 255)
    @NotBlank(groups = Creacion.class)
    @Pattern(regexp = ".*\\S.*")
    private String nombres;

    @Size(max = 255)
    @NotBlank(groups = Creacion.class)
    @Pattern(regexp = ".*\\S.*")
    private String aPaterno;

    @Size(max = 255)
    private String aMaterno;

    @Min(value = 1)
    private Long rut;

    @Size(max = 1)
    private String dv;

    @NotNull(groups = Creacion.class)
    private LocalDate fechaNacimiento;

    @Size(max = 300)
    private String direccion;

    @Email
    @Size(max = 100)
    @NotBlank(groups = Creacion.class)
    @Pattern(regexp = ".*\\S.*")
    private String correo;

    @Min(value = 1)
    @NotNull(groups = Creacion.class)
    private Long idRegion;

    @Min(value = 1)
    @NotNull(groups = Creacion.class)
    private Long idComuna;

    @Min(value = 1)
    private Long idDireccion;
}