package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.rolUsuario.RolUsuarioDTORequest;
import com.esam.esam_backend.exception.RolUsuarioConUsuariosException;
import com.esam.esam_backend.exception.RolUsuarioNoEncontradaException;
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
        return rRepo.save(rMapper.toEntity(request));
    }

    // Editar
    public RolUsuario editar(Long id, RolUsuarioDTORequest request) {
        RolUsuario rol = obtenerRol(id);

        rol.setNombre(request.getNombre());

        return rRepo.save(rol);
    }

    // Borrar
    public void borrar(Long id) {
        RolUsuario rol = obtenerRol(id);

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