package com.esam.esam_backend.dto.usuario;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTOResponse {

    private Long id;
    private String nombres;
    private String aPaterno;
    private String aMaterno;
    private String correo;
    private String region;
    private String comuna;
    private String rol;
    private String nombreUsuario;
}
