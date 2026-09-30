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

import com.esam.esam_backend.dto.comuna.ComunaDTO;
import com.esam.esam_backend.dto.comuna.ComunaDTORequest;
import com.esam.esam_backend.mapper.ComunaMapper;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.service.ComunaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor 
@RequestMapping("/api/comunas")
public class ComunaController {

    private final ComunaService comunaService;

    private final ComunaMapper cMapper;

    // Obtener una comuna por su id
    @GetMapping("/{id}")
    public ResponseEntity<ComunaDTO> obtenerPorId(@PathVariable Long id) {

        Comuna comuna = comunaService.obtenerPorId(id);

        if(comuna == null){
            return ResponseEntity.notFound().build();
        }

        ComunaDTO dto = cMapper.toDTO(comuna);
        return ResponseEntity.ok(dto);
    }

    // Obtener todas las comunas
    @GetMapping
    public ResponseEntity<List<ComunaDTO>> obtenerTodos() {

        List<ComunaDTO> dtoList = cMapper.toDTOList(comunaService.obtenerTodos());

        return ResponseEntity.ok(dtoList);
    }

    // Guardar una comuna
    @PostMapping
    public ResponseEntity<ComunaDTO> guardar(@RequestBody @Valid ComunaDTORequest request) {
        
        Comuna comuna = comunaService.guardar(request);

        ComunaDTO dto = cMapper.toDTO(comuna);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Editar una comuna
    @PutMapping("/{id}")
    public ResponseEntity<ComunaDTO> editar(@PathVariable Long id, @RequestBody @Valid ComunaDTORequest request) {
        
        Comuna comuna = comunaService.editar(id, request);

        ComunaDTO dto = cMapper.toDTO(comuna);
        return ResponseEntity.ok(dto);
    }

    // Borrar una comuna
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        
        comunaService.borrar(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}