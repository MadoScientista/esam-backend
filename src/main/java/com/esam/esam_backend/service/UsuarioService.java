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
import com.esam.esam_backend.mapper.UsuarioDTORequestMapper;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.RegionRepository;
import com.esam.esam_backend.repository.RolUsuarioRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository uRepo;

    private final RolUsuarioRepository rRepo;

    private final RegionRepository regionRepo;

    private final ComunaRepository cRepo;

    private final UsuarioDTORequestMapper uMapper;

    private final PasswordEncoder passwordEncoder;

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

        Region region = obtenerRegion(request.getIdRegion());
        Comuna comuna = obtenerComuna(request.getIdComuna());
        
        //-------------------------------------
        // Arreglar
        //
        List<Direccion> direcciones = new ArrayList<>();

        Usuario usuario = uMapper.toEntity(request, rol, comuna, direcciones);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        return uRepo.save(usuario);
    }

    // Editar usuario. Solo lo usa el administrador: puede cambiar el rol.
    public Usuario editar(Long id, UsuarioAdminDTORequest request) {
        Usuario usuario = obtenerUsuario(id);

        RolUsuario rol = obtenerRol(request.getIdRolUsuario());

        actualizarDatos(usuario, request);
        usuario.setRolUsuario(rol);

        return uRepo.save(usuario);
    }

    // Editar el perfil propio. El rol no se toca aunque venga en el cuerpo.
    public Usuario editarPerfil(Long id, UsuarioDTORequest request) {
        Usuario usuario = obtenerUsuario(id);

        actualizarDatos(usuario, request);

        return uRepo.save(usuario);
    }

    private void actualizarDatos(Usuario usuario, UsuarioDTORequest request) {

        Comuna comuna = obtenerComuna(request.getIdComuna());
        Direccion direccion = obtenerDireccion(request.getIdDireccion());

        usuario.setNombres(request.getNombres());
        usuario.setAPaterno(request.getAPaterno());
        usuario.setAMaterno(request.getAMaterno());
        usuario.setRut(request.getRut());
        usuario.setDv(request.getDv());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.getDirecciones().add(direccion);
        usuario.setTelefono(request.getTelefono());
        usuario.setCorreo(request.getCorreo());
        usuario.setComuna(comuna);

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

    private Region obtenerRegion(Long idRegion) {
        return regionRepo.findById(idRegion)
                .orElseThrow(() -> new UsuarioInvalidaException("No existe una región con id " + idRegion));
    }

    private Comuna obtenerComuna(Long idComuna) {
        return cRepo.findById(idComuna)
                .orElseThrow(() -> new UsuarioInvalidaException("No existe una comuna con id " + idComuna));
    }

    private void validarPasswordNueva(String password) {
        if (password == null || password.isBlank()) {
            throw new UsuarioInvalidaException("La contraseña es obligatoria al crear un usuario");
        }
    }


    private Direccion obtenerDireccion(Long idDireccion) {
        return new Direccion();
    }
}