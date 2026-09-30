package com.esam.esam_backend.dto.usuario;

import java.time.LocalDate;

import com.esam.esam_backend.dto.comuna.ComunaDTO;
import com.esam.esam_backend.dto.region.RegionDTO;
import com.esam.esam_backend.dto.rolUsuario.RolUsuarioDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTOResponse {

    private Long id;
    private String nombres;
    private String aPaterno;
    private String aMaterno;
    private Long rut;
    private String dv;
    private String correo;
    private LocalDate fechaNacimiento;
    private String direccion;
    private Long telefono;
    private RegionDTO region;
    private ComunaDTO comuna;
    private RolUsuarioDTO rol;
    private String nombreUsuario;


}
