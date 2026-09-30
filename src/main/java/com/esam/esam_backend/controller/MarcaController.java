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

import com.esam.esam_backend.dto.marca.MarcaDTO;
import com.esam.esam_backend.dto.marca.MarcaDTORequest;
import com.esam.esam_backend.mapper.MarcaMapper;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.service.MarcaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private MarcaMapper mMapper;

    // Obtener una marca por su id
    @GetMapping("/{id}")
    public ResponseEntity<MarcaDTO> obtenerPorId(@PathVariable Long id) {

        Marca marca = marcaService.obtenerPorId(id);

        if(marca == null){
            return ResponseEntity.notFound().build();
        }

        MarcaDTO dto = mMapper.toDTO(marca);
        return ResponseEntity.ok(dto);
    }

    // Obtener todas las marcas
    @GetMapping
    public ResponseEntity<List<MarcaDTO>> obtenerTodos() {

        List<MarcaDTO> dtoList = mMapper.toDTOList(marcaService.obtenerTodos());

        return ResponseEntity.ok(dtoList);
    }

    // Guardar una marca
    @PostMapping
    public ResponseEntity<MarcaDTO> guardar(@RequestBody @Valid MarcaDTORequest request) {

        Marca marca = marcaService.guardar(request);

        MarcaDTO dto = mMapper.toDTO(marca);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Editar una marca
    @PutMapping("/{id}")
    public ResponseEntity<MarcaDTO> editar(@PathVariable Long id, @RequestBody @Valid MarcaDTORequest request) {

        Marca marca = marcaService.editar(id, request);

        MarcaDTO dto = mMapper.toDTO(marca);
        return ResponseEntity.ok(dto);
    }

    // Borrar una marca
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {

        marcaService.borrar(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Borrar una marca junto con todos sus productos
    @DeleteMapping("/{id}/cascada")
    public ResponseEntity<Void> borrarEnCascada(@PathVariable Long id) {

        marcaService.borrarEnCascada(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
