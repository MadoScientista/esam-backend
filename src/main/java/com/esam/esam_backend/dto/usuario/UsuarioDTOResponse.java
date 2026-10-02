package com.esam.esam_backend.dto.usuario;

import java.time.Instant;
import java.time.LocalDate;

import com.esam.esam_backend.dto.comuna.ComunaDTO;
import com.esam.esam_backend.dto.region.RegionDTO;
import com.esam.esam_backend.dto.rolUsuario.RolUsuarioDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTOResponse {

    private Long idUsuario;
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rol;
    private Instant creadoEn;
    private String nombres;
    private String aPaterno;
    private String aMaterno;
    private Long rut;
    private String dv;
    private String correo;
    private LocalDate fechaNacimiento;
    private String direccion;
    private RegionDTO region;
    private ComunaDTO comuna;
    private RolUsuarioDTO rolDetalle;

    public void setRol(RolUsuarioDTO rolDetalle) {
        this.rolDetalle = rolDetalle;
        this.rol = rolDetalle == null ? null : rolDetalle.getNombre();
    }

    public void setRol(String rol) {
        this.rol = rol;
        this.rolDetalle = null;
    }
}
