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

import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.service.RegionService;

@RestController
@RequestMapping("/api/regiones")
public class RegionController {

    @Autowired
    private RegionService regionService;

    // Obtener una región por su id
    @GetMapping("/{id}")
    public Region obtenerPorId(@PathVariable Long id) {
        return regionService.obtenerPorId(id);
    }

    // Obtener todas las regiones
    @GetMapping
    public List<Region> obtenerTodos() {
        return regionService.obtenerTodos();
    }

    // Guardar una región
    @PostMapping
    public Region guardar(@RequestBody Region region) {
        return regionService.guardar(region);
    }

    // Editar una región
    @PutMapping("/{id}")
    public Region editar(@PathVariable Long id, @RequestBody Region region) {
        return regionService.editar(id, region);
    }

    // Borrar una región
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        regionService.borrar(id);
    }
}