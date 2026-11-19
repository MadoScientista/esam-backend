package com.esam.esam_backend.service;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.esam.esam_backend.dto.usuario.UsuarioAdminDTORequest;
import com.esam.esam_backend.dto.usuario.UsuarioDTORequest;
import com.esam.esam_backend.enums.RolSistema;
import com.esam.esam_backend.exception.UsuarioInvalidaException;
import com.esam.esam_backend.exception.UsuarioNoEncontradaException;
import com.esam.esam_backend.mapper.UsuarioMapper;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.RolUsuarioRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository uRepo;

    private final RolUsuarioRepository rRepo;

    private final ComunaRepository cRepo;

    private final UsuarioMapper usuarioMapper;

    private final PasswordEncoder passwordEncoder;
    private final DireccionService direccionService;

    // Obtener usuario según su id
    public Usuario obtenerPorId(Long id) {
        return uRepo.findById(id).orElse(null);
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodos() {
        return uRepo.findAll();
    }

    // Obtener usuarios según su rol
    public List<Usuario> obtenerPorRol(Long idRolUsuario) {
        return uRepo.findByRolUsuarioIdRolUsuario(idRolUsuario);
    }

    // Guardar usuario
    public Usuario guardar(UsuarioDTORequest request) {
        return guardarConRol(request, obtenerRolCliente());
    }

    // Guardar usuario eligiendo su rol. Solo lo usa el administrador.
    public Usuario guardar(UsuarioAdminDTORequest request) {
        return guardarConRol(request, obtenerRol(request.getIdRolUsuario()));
    }

    private Usuario guardarConRol(UsuarioDTORequest request, RolUsuario rol) {
        validarPasswordNueva(request.getPassword());

        Comuna comuna = obtenerComuna(request.getIdComuna());
        validarRegionDeComuna(request.getIdRegion(), comuna);
        
        //-------------------------------------
        // Arreglar
        //
        List<Direccion> direcciones = new ArrayList<>();

        Usuario usuario = usuarioMapper.toEntity(request, rol, comuna, direcciones);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        return uRepo.save(usuario);
    }

    // Editar usuario. Solo lo usa el administrador: puede cambiar el rol.
    public Usuario editar(Long id, UsuarioAdminDTORequest request) {
        Usuario usuario = obtenerUsuario(id);

        actualizarDatos(usuario, request);
        if (request.getIdRolUsuario() != null) {
            usuario.setRolUsuario(obtenerRol(request.getIdRolUsuario()));
        }

        return uRepo.save(usuario);
    }

    // Editar el perfil propio. El rol no se toca aunque venga en el cuerpo.
    public Usuario editarPerfil(Long id, UsuarioDTORequest request) {
        Usuario usuario = obtenerUsuario(id);

        actualizarDatos(usuario, request);

        return uRepo.save(usuario);
    }

    private void actualizarDatos(Usuario usuario, UsuarioDTORequest request) {

        Comuna comuna = usuario.getComuna();
        if (request.getIdComuna() != null) {
            comuna = obtenerComuna(request.getIdComuna());
        }
        if (request.getIdRegion() != null) {
            if (comuna == null) {
                throw new UsuarioInvalidaException(
                        "Debe indicar una comuna para validar la región");
            }
            validarRegionDeComuna(request.getIdRegion(), comuna);
        }
        Direccion direccion = request.getIdDireccion() == null
                ? null
                : direccionService.obtenerPorIdRequerida(request.getIdDireccion());

        if (request.getNombres() != null) {
            usuario.setNombres(request.getNombres());
        }
        if (request.getAPaterno() != null) {
            usuario.setAPaterno(request.getAPaterno());
        }
        if (request.getAMaterno() != null) {
            usuario.setAMaterno(request.getAMaterno());
        }
        if (request.getRut() != null) {
            usuario.setRut(request.getRut());
        }
        if (request.getDv() != null) {
            usuario.setDv(!request.getDv().isBlank() ? request.getDv() : null);
        }
        if (request.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(request.getFechaNacimiento());
        }
        if (direccion != null) {
            usuario.getDirecciones().add(direccion);
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getCorreo() != null) {
            usuario.setCorreo(request.getCorreo());
        }
        if (request.getIdComuna() != null) {
            usuario.setComuna(comuna);
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    // Borrar usuario
    public void borrar(Long id) {
        Usuario usuario = obtenerUsuario(id);
        uRepo.delete(usuario);
    }

    private Usuario obtenerUsuario(Long id) {
        Usuario usuario = obtenerPorId(id);

        if (usuario == null) {
            throw new UsuarioNoEncontradaException("No existe un usuario con id " + id);
        }

        return usuario;
    }

    private RolUsuario obtenerRol(Long idRolUsuario) {
        return rRepo.findById(idRolUsuario)
                .orElseThrow(() -> new UsuarioInvalidaException("No existe un rol de usuario con id " + idRolUsuario));
    }

    // El registro publico siempre nace como "cliente", se resuelva por nombre
    // y no por un id fijo.
    private RolUsuario obtenerRolCliente() {
        return rRepo.findByNombre(RolSistema.CLIENTE.getNombre())
                .orElseThrow(() ->
                    new UsuarioInvalidaException("No existe el rol de usuario " + RolSistema.CLIENTE.getNombre()));
    }

    private void validarRegionDeComuna(Long idRegion, Comuna comuna) {
        if (!comuna.getRegion().getIdRegion().equals(idRegion)) {
            throw new UsuarioInvalidaException(
                    "La comuna " + comuna.getIdComuna() + " no pertenece a la región " + idRegion);
        }
    }

    private Comuna obtenerComuna(Long idComuna) {
        return cRepo.findById(idComuna)
                .orElseThrow(() -> new UsuarioInvalidaException("No existe una comuna con id " + idComuna));
    }

    private void validarPasswordNueva(String password) {
        if (password == null || password.isBlank()) {
            throw new UsuarioInvalidaException("La contraseña es obligatoria al crear un usuario");
        }
        if (password.length() < 8 || password.length() > 72) {
            throw new UsuarioInvalidaException("La contraseña debe tener entre 8 y 72 caracteres");
        }
    }
}