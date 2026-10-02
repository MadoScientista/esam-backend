package com.esam.esam_backend.dto.usuario;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTOLoginResponse {

    private boolean loggin;
    private String token;
    private String tipoToken;
    private Instant expiraEn;
    private UsuarioDTOResponse usuario;
}
