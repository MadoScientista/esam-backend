package com.esam.esam_backend.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.esam.esam_backend.enums.RolSistema;
import com.esam.esam_backend.exception.UsuarioInvalidaException;
import com.esam.esam_backend.model.IdentidadExterna;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.IdentidadExternaRepository;
import com.esam.esam_backend.repository.RolUsuarioRepository;
import com.esam.esam_backend.repository.UsuarioRepository;
import com.esam.esam_backend.security.AutenticacionGoogleException;
import com.esam.esam_backend.security.GoogleIdentity;
import com.esam.esam_backend.security.GoogleIdTokenVerifier;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private static final String PROVEEDOR_GOOGLE = "google";

    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final IdentidadExternaRepository identidadExternaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolUsuarioRepository;

    @Transactional
    public Usuario autenticar(String idToken) {
        GoogleIdentity google = googleIdTokenVerifier.verificar(idToken);
        IdentidadExterna identidad = identidadExternaRepository
                .findByProveedorAndSubject(PROVEEDOR_GOOGLE, google.subject())
                .orElseGet(() -> crearVinculo(google));

        Usuario usuario = identidad.getUsuario();
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new AutenticacionGoogleException();
        }
        return usuario;
    }

    private IdentidadExterna crearVinculo(GoogleIdentity google) {
        String correo = google.email().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseGet(() -> crearUsuarioGoogle(google, correo));

        IdentidadExterna identidad = new IdentidadExterna();
        identidad.setProveedor(PROVEEDOR_GOOGLE);
        identidad.setSubject(google.subject());
        identidad.setUsuario(usuario);
        return identidadExternaRepository.save(identidad);
    }

    private Usuario crearUsuarioGoogle(GoogleIdentity google, String correo) {
        RolUsuario rolCliente = rolUsuarioRepository.findByNombre(RolSistema.CLIENTE.getNombre())
                .orElseThrow(() -> new UsuarioInvalidaException(
                        "No existe el rol de usuario " + RolSistema.CLIENTE.getNombre()));

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setPassword(null);
        usuario.setActivo(true);
        usuario.setRolUsuario(rolCliente);
        usuario.setNombres(nombreLocal(google.givenName()));
        usuario.setAPaterno(nombreLocal(google.familyName()));
        return usuarioRepository.save(usuario);
    }

    private String nombreLocal(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        String nombre = valor.trim();
        return nombre.length() <= 25 ? nombre : null;
    }
}
