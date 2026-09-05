package com.esam.esam_backend.dto.usuario;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTORequest {

    private String nombres;
    private String aPaterno;
    private String aMaterno;
    private Long rut;
    private String dv;
    private LocalDate fechaNacimiento;
    private String direccion;
    private Long telefono;
    private String nombreUsuario;
    private String correo;
    private String password;
    private Long idRolUsuario;
    private Long idRegion;
    private Long idComuna;
}