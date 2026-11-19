package com.esam.esam_backend.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;

class UsuarioMapperTests {

    @Test
    void perfilEsCompletoSinComunaCuandoTieneLosDatosMinimos() {
        Usuario usuario = new Usuario();
        usuario.setNombres("Nombre");
        usuario.setAPaterno("Apellido");
        usuario.setCorreo("usuario@ejemplo.cl");
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setRolUsuario(new RolUsuario());

        UsuarioDTOResponse response = new UsuarioMapper().toDTO(usuario);

        assertTrue(response.isPerfilCompleto());
    }
}
