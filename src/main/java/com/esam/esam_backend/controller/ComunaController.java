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

import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.service.ComunaService;

@RestController
@RequestMapping("/api/comunas")
public class ComunaController {

    @Autowired
    private ComunaService comunaService;

    // Obtener una comuna por su id
    @GetMapping("/{id}")
    public Comuna obtenerPorId(@PathVariable Long id) {
        return comunaService.obtenerPorId(id);
    }

    // Obtener todas las comunas
    @GetMapping
    public List<Comuna> obtenerTodos() {
        return comunaService.obtenerTodos();
    }

    // Guardar una comuna
    @PostMapping
    public Comuna guardar(@RequestBody Comuna comuna) {
        return comunaService.guardar(comuna);
    }

    // Editar una comuna
    @PutMapping("/{id}")
    public Comuna editar(@PathVariable Long id, @RequestBody Comuna comuna) {
        return comunaService.editar(id, comuna);
    }

    // Borrar una comuna
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        comunaService.borrar(id);
    }
}