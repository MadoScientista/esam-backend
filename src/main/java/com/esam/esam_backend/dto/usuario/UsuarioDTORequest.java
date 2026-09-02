package com.esam.esam_backend.dto.usuario;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTORequest {

    private String pNombre;
    private String sNombre;
    private String aPaterno;
    private String aMaterno;
    private String nombreUsuario;
    private String password;
    private Long idRol;
    private Long idRegion;
    private Long idComuna;
}