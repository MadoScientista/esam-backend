package com.esam.esam_backend.dto.usuario;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTOResponse {

    private Long id;
    private String pNombre;
    private String sNombre;
    private String aPaterno;
    private String aMaterno;
    private String rol;
    private String nombreUsuario;
}
