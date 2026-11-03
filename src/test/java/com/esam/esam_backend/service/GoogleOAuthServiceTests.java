package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.esam.esam_backend.enums.RolSistema;
import com.esam.esam_backend.model.IdentidadExterna;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.IdentidadExternaRepository;
import com.esam.esam_backend.repository.RolUsuarioRepository;
import com.esam.esam_backend.repository.UsuarioRepository;
import com.esam.esam_backend.security.AutenticacionGoogleException;
import com.esam.esam_backend.security.GoogleIdentity;
import com.esam.esam_backend.security.GoogleIdTokenVerifier;

@ExtendWith(MockitoExtension.class)
class GoogleOAuthServiceTests {

    @Mock
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @Mock
    private IdentidadExternaRepository identidadExternaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolUsuarioRepository rolUsuarioRepository;

    @InjectMocks
    private GoogleOAuthService googleOAuthService;

    @Test
    void vinculaCuentaExistentePorCorreoVerificadoSinAlterarSusDatos() {
        GoogleIdentity google = new GoogleIdentity(
                "google-subject", "CLIENTE@ejemplo.cl", "Nombre", "Apellido");
        Usuario cuentaExistente = new Usuario();
        cuentaExistente.setCorreo("cliente@ejemplo.cl");
        when(googleIdTokenVerifier.verificar("id-token")).thenReturn(google);
        when(identidadExternaRepository.findByProveedorAndSubject("google", "google-subject"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByCorreoIgnoreCase("cliente@ejemplo.cl"))
                .thenReturn(Optional.of(cuentaExistente));
        when(identidadExternaRepository.save(any(IdentidadExterna.class)))
                .thenAnswer(invocation -> {
                    IdentidadExterna identidad = invocation.getArgument(0);
                    return identidad;
                });

        Usuario autenticado = googleOAuthService.autenticar("id-token");

        assertSame(cuentaExistente, autenticado);
        verify(usuarioRepository).findByCorreoIgnoreCase("cliente@ejemplo.cl");
        verify(identidadExternaRepository).save(any(IdentidadExterna.class));
        verifyNoInteractions(rolUsuarioRepository);
    }

    @Test
    void usaLaIdentidadVinculadaAunqueGoogleInformeOtroCorreo() {
        Usuario cuentaVinculada = new Usuario();
        cuentaVinculada.setCorreo("correo-anterior@ejemplo.cl");
        IdentidadExterna identidad = new IdentidadExterna();
        identidad.setUsuario(cuentaVinculada);

        when(googleIdTokenVerifier.verificar("id-token"))
                .thenReturn(new GoogleIdentity("google-subject", "correo-nuevo@ejemplo.cl", null, null));
        when(identidadExternaRepository.findByProveedorAndSubject("google", "google-subject"))
                .thenReturn(Optional.of(identidad));

        Usuario autenticado = googleOAuthService.autenticar("id-token");

        assertSame(cuentaVinculada, autenticado);
        verifyNoInteractions(usuarioRepository, rolUsuarioRepository);
    }

    @Test
    void creaCuentaClienteSinPasswordYConPerfilIncompleto() {
        GoogleIdentity google = new GoogleIdentity(
                "google-subject", "nuevo@ejemplo.cl", "Ana", "Pérez");
        RolUsuario rolCliente = new RolUsuario();
        rolCliente.setNombre(RolSistema.CLIENTE.getNombre());

        when(googleIdTokenVerifier.verificar("id-token")).thenReturn(google);
        when(identidadExternaRepository.findByProveedorAndSubject("google", "google-subject"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByCorreoIgnoreCase("nuevo@ejemplo.cl"))
                .thenReturn(Optional.empty());
        when(rolUsuarioRepository.findByNombre(RolSistema.CLIENTE.getNombre()))
                .thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(identidadExternaRepository.save(any(IdentidadExterna.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario creado = googleOAuthService.autenticar("id-token");

        assertEquals("nuevo@ejemplo.cl", creado.getCorreo());
        assertEquals("Ana", creado.getNombres());
        assertEquals("Pérez", creado.getAPaterno());
        assertSame(rolCliente, creado.getRolUsuario());
        assertNull(creado.getPassword());
        assertFalse(creado.esPerfilCompleto());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void rechazaCuentaLocalInactivaSinExponerSuExistencia() {
        Usuario inactivo = new Usuario();
        inactivo.setActivo(false);
        IdentidadExterna identidad = new IdentidadExterna();
        identidad.setUsuario(inactivo);

        when(googleIdTokenVerifier.verificar("id-token"))
                .thenReturn(new GoogleIdentity("google-subject", "usuario@ejemplo.cl", null, null));
        when(identidadExternaRepository.findByProveedorAndSubject("google", "google-subject"))
                .thenReturn(Optional.of(identidad));

        assertThrows(AutenticacionGoogleException.class,
                () -> googleOAuthService.autenticar("id-token"));
        verifyNoInteractions(usuarioRepository, rolUsuarioRepository);
    }
}
