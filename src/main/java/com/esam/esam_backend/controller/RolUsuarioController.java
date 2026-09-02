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

import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.service.RolUsuarioService;

@RestController
@RequestMapping("/api/roles")
public class RolUsuarioController {

    @Autowired
    private RolUsuarioService rolUsuarioService;

    // Obtener un rol por su id
    @GetMapping("/{id}")
    public RolUsuario obtenerPorId(@PathVariable Long id) {
        return rolUsuarioService.obtenerPorId(id);
    }

    // Obtener todos los roles
    @GetMapping
    public List<RolUsuario> obtenerTodos() {
        return rolUsuarioService.obtenerTodos();
    }

    // Guardar un rol
    @PostMapping
    public RolUsuario guardar(@RequestBody RolUsuario rol) {
        return rolUsuarioService.guardar(rol);
    }

    // Editar un rol
    @PutMapping("/{id}")
    public RolUsuario editar(@PathVariable Long id, @RequestBody RolUsuario rol) {
        return rolUsuarioService.editar(id, rol);
    }

    // Borrar un rol
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        rolUsuarioService.borrar(id);
    }
}