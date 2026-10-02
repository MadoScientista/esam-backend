package com.esam.esam_backend.dto.usuario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// Cuerpo de las operaciones de administracion de usuarios.
// El registro publico (UsuarioDTORequest) no puede elegir rol: siempre nace
// como "cliente". Solo un administrador usa este DTO.
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UsuarioAdminDTORequest extends UsuarioDTORequest {

    @NotNull
    @Min(value = 1)
    private Long idRolUsuario;

}