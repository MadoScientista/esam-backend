package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.rolUsuario.RolUsuarioDTORequest;
import com.esam.esam_backend.exception.RolUsuarioConUsuariosException;
import com.esam.esam_backend.exception.RolUsuarioDuplicadoException;
import com.esam.esam_backend.exception.RolUsuarioNoEncontradaException;
import com.esam.esam_backend.exception.RolUsuarioSistemaException;
import com.esam.esam_backend.mapper.RolUsuarioMapper;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.repository.RolUsuarioRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RolUsuarioService {

    private final RolUsuarioRepository rRepo;

    private final UsuarioRepository uRepo;

    private final RolUsuarioMapper rMapper;

    // Obtener según su id
    public RolUsuario obtenerPorId(Long id) {
        return rRepo.findById(id).orElse(null);
    }

    // Obtener todos
    public List<RolUsuario> obtenerTodos() {
        return rRepo.findAll();
    }

    // Guardar
    public RolUsuario guardar(RolUsuarioDTORequest request) {
        String nombre = request.getNombre();

        if (nombre == null || nombre.isBlank()) {
            throw new RolUsuarioSistemaException("El nombre del rol es obligatorio");
        }

        if (rRepo.findByNombre(nombre).isPresent()) {
            throw new RolUsuarioDuplicadoException("Ya existe un rol con el nombre " + nombre);
        }

        return rRepo.save(rMapper.toEntity(request));
    }

    // Editar
    public RolUsuario editar(Long id, RolUsuarioDTORequest request) {
        RolUsuario rol = obtenerRol(id);

        // // Los roles del sistema no se renombran: el registro publico y las
        // // reglas de autorizacion los buscan por nombre.
        // if (rol.esDelSistema()) {
        //     throw new RolUsuarioSistemaException(
        //             "El rol " + rol.getNombre() + " es del sistema y no se puede renombrar");
        // }

        String nombre = request.getNombre();

        if (nombre != null && !nombre.isBlank()) {
            RolUsuario existente = rRepo.findByNombre(nombre).orElse(null);

            if (existente != null && !existente.getIdRolUsuario().equals(id)) {
                throw new RolUsuarioDuplicadoException("Ya existe un rol con el nombre " + nombre);
            }
        }

        rol.setNombre(request.getNombre());

        return rRepo.save(rol);
    }

    // Borrar
    public void borrar(Long id) {
        RolUsuario rol = obtenerRol(id);

        // if (rol.esDelSistema()) {
        //     throw new RolUsuarioSistemaException(
        //             "El rol " + rol.getNombre() + " es del sistema y no se puede eliminar");
        // }

        long usuariosAsociados = uRepo.countByRolUsuarioIdRolUsuario(id);

        if (usuariosAsociados > 0) {
            throw new RolUsuarioConUsuariosException(
                    "El rol " + id + " tiene " + usuariosAsociados + " usuario(s) asociado(s)");
        }

        rRepo.delete(rol);
    }

    private RolUsuario obtenerRol(Long id) {
        RolUsuario rol = obtenerPorId(id);

        if (rol == null) {
            throw new RolUsuarioNoEncontradaException("No existe un rol de usuario con id " + id);
        }

        return rol;
    }
}