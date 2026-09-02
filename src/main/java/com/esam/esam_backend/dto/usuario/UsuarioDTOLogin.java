package com.esam.esam_backend.dto.usuario;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTOLogin {

    private String nombreUsuario;
    private String password;
}
