package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.usuario.UsuarioDTORequest;
import com.esam.esam_backend.exception.UsuarioInvalidaException;
import com.esam.esam_backend.exception.UsuarioNoEncontradaException;
import com.esam.esam_backend.mapper.UsuarioDTORequestMapper;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.RegionRepository;
import com.esam.esam_backend.repository.RolUsuarioRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository uRepo;

    @Autowired
    private RolUsuarioRepository rRepo;

    @Autowired
    private RegionRepository regionRepo;

    @Autowired
    private ComunaRepository cRepo;

    @Autowired
    private UsuarioDTORequestMapper uMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        validarPasswordNueva(request.getPassword());

        RolUsuario rol = obtenerRol(request.getIdRolUsuario());
        Region region = obtenerRegion(request.getIdRegion());
        Comuna comuna = obtenerComuna(request.getIdComuna());

        Usuario usuario = uMapper.toEntity(request, rol, region, comuna);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        return uRepo.save(usuario);
    }

    // Editar usuario
    public Usuario editar(Long id, UsuarioDTORequest request) {
        Usuario usuario = obtenerUsuario(id);

        RolUsuario rol = obtenerRol(request.getIdRolUsuario());
        Region region = obtenerRegion(request.getIdRegion());
        Comuna comuna = obtenerComuna(request.getIdComuna());

        usuario.setNombres(request.getNombres());
        usuario.setAPaterno(request.getAPaterno());
        usuario.setAMaterno(request.getAMaterno());
        usuario.setRut(request.getRut());
        usuario.setDv(request.getDv());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setDireccion(request.getDireccion());
        usuario.setTelefono(request.getTelefono());
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setRolUsuario(rol);
        usuario.setRegion(region);
        usuario.setComuna(comuna);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return uRepo.save(usuario);
    }

    // Borrar usuario
    public void borrar(Long id) {
        Usuario usuario = obtenerUsuario(id);
        uRepo.delete(usuario);
    }

    // Confirmar login
    public boolean confirmarLogin(String nombreUsuario, String password) {
        Usuario usuario = uRepo.findByNombreUsuario(nombreUsuario);

        if (usuario == null || password == null || usuario.getPassword() == null) {
            return false;
        }

        return passwordEncoder.matches(password, usuario.getPassword());
    }

    // Obtener usuario según su nombre de usuario
    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        return uRepo.findByNombreUsuario(nombreUsuario);
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
}