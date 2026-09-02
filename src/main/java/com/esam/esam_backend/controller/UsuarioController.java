package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esam.esam_backend.dto.usuario.UsuarioDTOLogin;
import com.esam.esam_backend.dto.usuario.UsuarioDTOLoginResponse;
import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse;
import com.esam.esam_backend.mapper.UsuarioMapper;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private final UsuarioMapper usuarioMapper = new UsuarioMapper();

    // Obtener un usuario por su id
    @GetMapping("/{id}")
    public UsuarioDTOResponse obtenerPorId(@PathVariable Long id) {
        return usuarioMapper.toDTO(usuarioService.obtenerPorId(id));
    }

    // Obtener todos los usuarios
    @GetMapping
    public List<UsuarioDTOResponse> obtenerTodos() {
        return usuarioMapper.toDTOList(usuarioService.obtenerTodos());
    }

    // Obtener usuarios según su rol
    @GetMapping("/rol/{idRolUsuario}")
    public List<UsuarioDTOResponse> obtenerPorRol(@PathVariable Long idRolUsuario) {
        return usuarioMapper.toDTOList(usuarioService.obtenerPorRol(idRolUsuario));
    }

    // Confirmar login
    @PostMapping("/login")
    public UsuarioDTOLoginResponse login(@RequestBody UsuarioDTOLogin login) {
        boolean loggin = usuarioService.confirmarLogin(login.getNombreUsuario(), login.getPassword());
        return new UsuarioDTOLoginResponse(loggin);
    }

    // Guardar un usuario
    @PostMapping
    public UsuarioDTOResponse guardar(@RequestBody Usuario usuario) {
        return usuarioMapper.toDTO(usuarioService.guardar(usuario));
    }

    // Editar un usuario
    @PutMapping("/{id}")
    public UsuarioDTOResponse editar(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioMapper.toDTO(usuarioService.editar(id, usuario));
    }

    // Borrar un usuario
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        usuarioService.borrar(id);
    }
}