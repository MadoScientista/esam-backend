package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository uRepo;

    // Obtener usuario según su id
    public Usuario obtenerPorId(Long id) {
        return uRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
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
    public Usuario guardar(Usuario usuario) {
        return uRepo.save(usuario);
    }

    // Editar usuario
    public Usuario editar(Long id, Usuario datos) {
        Usuario usuario = obtenerPorId(id);
        usuario.setNombres(datos.getNombres());
        usuario.setAPaterno(datos.getAPaterno());
        usuario.setAMaterno(datos.getAMaterno());
        usuario.setRut(datos.getRut());
        usuario.setDv(datos.getDv());
        usuario.setFechaNacimiento(datos.getFechaNacimiento());
        usuario.setDireccion(datos.getDireccion());
        usuario.setTelefono(datos.getTelefono());
        usuario.setNombreUsuario(datos.getNombreUsuario());
        usuario.setCorreo(datos.getCorreo());
        usuario.setPassword(datos.getPassword());
        usuario.setRolUsuario(datos.getRolUsuario());
        return uRepo.save(usuario);
    }

    // Borrar usuario
    public void borrar(Long id) {
        Usuario usuario = obtenerPorId(id);
        uRepo.delete(usuario);
    }

    // Confirmar login
    public boolean confirmarLogin(String nombreUsuario, String password) {
        Usuario usuario = uRepo.findByNombreUsuario(nombreUsuario);
        return usuario != null && usuario.getPassword().equals(password);
    }

    // Obtener usuario según su nombre de usuario
    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        return uRepo.findByNombreUsuario(nombreUsuario);
    }
}
