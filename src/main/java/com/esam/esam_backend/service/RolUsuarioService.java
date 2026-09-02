package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.repository.RolUsuarioRepository;

@Service
public class RolUsuarioService {

    @Autowired
    private RolUsuarioRepository rRepo;

    // Obtener según su id
    public RolUsuario obtenerPorId(Long id) {
        return rRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol de usuario no encontrado con id: " + id));
    }

    // Obtener todos
    public List<RolUsuario> obtenerTodos() {
        return rRepo.findAll();
    }

    // Guardar
    public RolUsuario guardar(RolUsuario rol) {
        return rRepo.save(rol);
    }

    // Editar
    public RolUsuario editar(Long id, RolUsuario datos) {
        RolUsuario rol = obtenerPorId(id);
        rol.setNombre(datos.getNombre());
        return rRepo.save(rol);
    }

    // Borrar
    public void borrar(Long id) {
        RolUsuario rol = obtenerPorId(id);
        rRepo.delete(rol);
    }
}
