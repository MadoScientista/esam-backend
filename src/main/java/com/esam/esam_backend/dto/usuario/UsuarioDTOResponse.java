package com.esam.esam_backend.dto.usuario;

import java.time.LocalDate;

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
    private RolDTO rol;
    private String nombreUsuario;

    @Data
    @NoArgsConstructor
    public static class RegionDTO {
        private Long idRegion;
        private String nombre;
    }

    @Data
    @NoArgsConstructor
    public static class ComunaDTO {
        private Long idComuna;
        private String nombre;
    }

    @Data
    @NoArgsConstructor
    public static class RolDTO {
        private Long idRolUsuario;
        private String nombre;
    }
}
