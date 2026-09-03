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

import com.esam.esam_backend.dto.marca.MarcaDTO;
import com.esam.esam_backend.mapper.MarcaMapper;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.service.MarcaService;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    private final MarcaMapper marcaMapper = new MarcaMapper();

    // Obtener una marca por su id
    @GetMapping("/{id}")
    public MarcaDTO obtenerPorId(@PathVariable Long id) {
        return marcaMapper.toDTO(marcaService.obtenerPorId(id));
    }

    // Obtener todas las marcas
    @GetMapping
    public List<MarcaDTO> obtenerTodos() {
        return marcaMapper.toDTOList(marcaService.obtenerTodos());
    }

    // Guardar una marca
    @PostMapping
    public Marca guardar(@RequestBody Marca marca) {
        return marcaService.guardar(marca);
    }

    // Editar una marca
    @PutMapping("/{id}")
    public Marca editar(@PathVariable Long id, @RequestBody Marca marca) {
        return marcaService.editar(id, marca);
    }

    // Borrar una marca
    @DeleteMapping("/{id}")
    public void borrar(@PathVariable Long id) {
        marcaService.borrar(id);
    }
}