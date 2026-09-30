package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.esam.esam_backend.dto.rolUsuario.RolUsuarioDTO;
import com.esam.esam_backend.dto.rolUsuario.RolUsuarioDTORequest;
import com.esam.esam_backend.mapper.RolUsuarioMapper;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.service.RolUsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RolUsuarioController {

    @Autowired
    private RolUsuarioService rolUsuarioService;

    @Autowired
    private RolUsuarioMapper rolUsuarioMapper;

    // Obtener un rol por su id
    @GetMapping("/{id}")
    public ResponseEntity<RolUsuarioDTO> obtenerPorId(@PathVariable Long id) {

        RolUsuario rol = rolUsuarioService.obtenerPorId(id);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        RolUsuarioDTO dto = rolUsuarioMapper.toDTO(rol);
        return ResponseEntity.ok(dto);
    }

    // Obtener todos los roles
    @GetMapping
    public ResponseEntity<List<RolUsuarioDTO>> obtenerTodos() {

        List<RolUsuarioDTO> dtoList = rolUsuarioMapper.toDTOList(rolUsuarioService.obtenerTodos());

        return ResponseEntity.ok(dtoList);
    }

    // Guardar un rol
    @PostMapping
    public ResponseEntity<RolUsuarioDTO> guardar(@RequestBody @Valid RolUsuarioDTORequest request) {

        RolUsuario rol = rolUsuarioService.guardar(request);

        RolUsuarioDTO dto = rolUsuarioMapper.toDTO(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Editar un rol
    @PutMapping("/{id}")
    public ResponseEntity<RolUsuarioDTO> editar(@PathVariable Long id, @RequestBody @Valid RolUsuarioDTORequest request) {

        RolUsuario rol = rolUsuarioService.editar(id, request);

        RolUsuarioDTO dto = rolUsuarioMapper.toDTO(rol);
        return ResponseEntity.ok(dto);
    }

    // Borrar un rol
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {

        rolUsuarioService.borrar(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}