package com.esam.esam_backend.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioDTOLogin {

    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    @NotBlank
    private String password;
}
