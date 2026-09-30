package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.esam.esam_backend.dto.usuario.UsuarioDTORequest;
import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse;
import com.esam.esam_backend.mapper.UsuarioMapper;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor 
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final UsuarioMapper usuarioMapper;

    // Obtener un usuario por su id
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTOResponse> obtenerPorId(@PathVariable Long id) {

        Usuario usuario = usuarioService.obtenerPorId(id);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        UsuarioDTOResponse dto = usuarioMapper.toDTO(usuario);
        return ResponseEntity.ok(dto);
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTOResponse>> obtenerTodos() {

        List<UsuarioDTOResponse> dtoList = usuarioMapper.toDTOList(usuarioService.obtenerTodos());

        return ResponseEntity.ok(dtoList);
    }

    // Obtener usuarios según su rol
    @GetMapping("/rol/{idRolUsuario}")
    public ResponseEntity<List<UsuarioDTOResponse>> obtenerPorRol(@PathVariable Long idRolUsuario) {

        List<UsuarioDTOResponse> dtoList = usuarioMapper.toDTOList(usuarioService.obtenerPorRol(idRolUsuario));

        return ResponseEntity.ok(dtoList);
    }

    // Confirmar login
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTOLoginResponse> login(@RequestBody @Valid UsuarioDTOLogin login) {

        boolean loggin = usuarioService.confirmarLogin(login.getCorreo(), login.getPassword());

        UsuarioDTOLoginResponse response = new UsuarioDTOLoginResponse();
        response.setLoggin(loggin);

        if (loggin) {
            Usuario usuario = usuarioService.obtenerPorCorreo(login.getCorreo());
            response.setUsuario(usuarioMapper.toDTO(usuario));
        }

        return ResponseEntity.ok(response);
    }

    // Guardar un usuario
    @PostMapping
    public ResponseEntity<UsuarioDTOResponse> guardar(@RequestBody @Valid UsuarioDTORequest request) {

        Usuario usuario = usuarioService.guardar(request);

        UsuarioDTOResponse dto = usuarioMapper.toDTO(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Editar un usuario
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTOResponse> editar(@PathVariable Long id, @RequestBody @Valid UsuarioDTORequest request) {

        Usuario usuario = usuarioService.editar(id, request);

        UsuarioDTOResponse dto = usuarioMapper.toDTO(usuario);
        return ResponseEntity.ok(dto);
    }

    // Borrar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {

        usuarioService.borrar(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}