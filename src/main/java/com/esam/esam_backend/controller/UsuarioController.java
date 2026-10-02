package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esam.esam_backend.dto.usuario.UsuarioAdminDTORequest;
import com.esam.esam_backend.dto.usuario.UsuarioDTOLogin;
import com.esam.esam_backend.dto.usuario.UsuarioDTOLoginResponse;
import com.esam.esam_backend.dto.usuario.UsuarioDTORequest;
import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse;
import com.esam.esam_backend.mapper.UsuarioMapper;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.security.CustomUserDetails;
import com.esam.esam_backend.security.JwtService;
import com.esam.esam_backend.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor 
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
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

    // Obtener el perfil del usuario autenticado
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTOResponse> obtenerPerfil(
            @AuthenticationPrincipal CustomUserDetails principal) {

        Usuario usuario = usuarioService.obtenerPorId(principal.getUsuario().getIdUsuario());

        return ResponseEntity.ok(usuarioMapper.toDTO(usuario));
    }

    // Confirmar login
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTOLoginResponse> login(
            @RequestBody @Valid UsuarioDTOLogin login) {

        try{

            Authentication authentication = 
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        login.getCorreo(), login.getPassword())
                );

            // authenticate() ya devolvió el usuario cargado: consultarlo otra
            // vez por correo era redundante, y devolvía null (con NPE al
            // mapear) si el casing no coincidía con el almacenado.
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

            String token = jwtService.generarToken(principal);

            UsuarioDTOLoginResponse response = new UsuarioDTOLoginResponse();

            response.setLoggin(true);
            response.setToken(token);
            response.setUsuario(usuarioMapper.toDTO(principal.getUsuario()));

            return ResponseEntity.ok(response);

        }catch (BadCredentialsException e){
            UsuarioDTOLoginResponse response = new UsuarioDTOLoginResponse();
            response.setLoggin(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
    }

    // Registrar un usuario. Es público, pero el rol queda fijo en "cliente".
    @PostMapping
    public ResponseEntity<UsuarioDTOResponse> guardar(@RequestBody @Valid UsuarioDTORequest request) {

        Usuario usuario = usuarioService.guardar(request);

        UsuarioDTOResponse dto = usuarioMapper.toDTO(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Crear un usuario eligiendo su rol. Solo administradores.
    @PostMapping("/admin")
    public ResponseEntity<UsuarioDTOResponse> guardarPorAdmin(
            @RequestBody @Valid UsuarioAdminDTORequest request) {

        Usuario usuario = usuarioService.guardar(request);

        UsuarioDTOResponse dto = usuarioMapper.toDTO(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Editar un usuario. Solo administradores: aquí sí se puede cambiar el rol.
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTOResponse> editar(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioAdminDTORequest request) {

        Usuario usuario = usuarioService.editar(id, request);

        UsuarioDTOResponse dto = usuarioMapper.toDTO(usuario);
        return ResponseEntity.ok(dto);
    }

    // Editar el perfil propio. El rol no se modifica.
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioDTOResponse> editarPerfil(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid UsuarioDTORequest request) {

        Usuario usuario = usuarioService.editarPerfil(principal.getUsuario().getIdUsuario(), request);

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