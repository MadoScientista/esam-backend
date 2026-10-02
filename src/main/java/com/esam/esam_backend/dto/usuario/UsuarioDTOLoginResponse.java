package com.esam.esam_backend.dto.usuario;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTOLoginResponse {

    private boolean loggin;
    private String token;
    private UsuarioDTOResponse usuario;
}
