package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.esam.esam_backend.dto.usuario.UsuarioDTORequest;
import com.esam.esam_backend.dto.usuario.UsuarioAdminDTORequest;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.RolUsuarioRepository;
import com.esam.esam_backend.repository.UsuarioRepository;
import com.esam.esam_backend.mapper.UsuarioMapper;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolUsuarioRepository rolUsuarioRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DireccionService direccionService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void editarPerfilActualizaSoloLosCamposEnviados() {
        Usuario usuario = usuarioExistente();
        UsuarioDTORequest request = new UsuarioDTORequest();
        request.setTelefono("+56912345678");

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario actualizado = usuarioService.editarPerfil(7L, request);

        assertEquals("+56912345678", actualizado.getTelefono());
        assertEquals("Nombre", actualizado.getNombres());
        assertEquals("Apellido", actualizado.getAPaterno());
        assertEquals("usuario@ejemplo.cl", actualizado.getCorreo());
        assertEquals(LocalDate.of(1990, 1, 1), actualizado.getFechaNacimiento());
        assertEquals(usuario.getRolUsuario(), actualizado.getRolUsuario());
        verify(usuarioRepository).save(usuario);
        verifyNoInteractions(rolUsuarioRepository, comunaRepository, usuarioMapper, passwordEncoder, direccionService);
    }

    @Test
    void editarUsuarioAdminSinRolEnElCuerpoConservaElRol() {
        Usuario usuario = usuarioExistente();
        UsuarioAdminDTORequest request = new UsuarioAdminDTORequest();
        request.setTelefono("+56912345678");

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario actualizado = usuarioService.editar(7L, request);

        assertEquals("+56912345678", actualizado.getTelefono());
        assertEquals(usuario.getRolUsuario(), actualizado.getRolUsuario());
        verify(usuarioRepository).save(usuario);
        verifyNoInteractions(rolUsuarioRepository, comunaRepository, usuarioMapper, passwordEncoder, direccionService);
    }

    private static Usuario usuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(7L);
        usuario.setNombres("Nombre");
        usuario.setAPaterno("Apellido");
        usuario.setCorreo("usuario@ejemplo.cl");
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setRolUsuario(new RolUsuario());
        usuario.setTelefono("+56911111111");
        return usuario;
    }
}
