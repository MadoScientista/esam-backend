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

import com.esam.esam_backend.dto.comuna.ComunaDTO;
import com.esam.esam_backend.dto.comuna.ComunaDTORequest;
import com.esam.esam_backend.mapper.ComunaMapper;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.service.ComunaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comunas")
public class ComunaController {

    @Autowired
    private ComunaService comunaService;

    @Autowired 
    private ComunaMapper cMapper;

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

        if(dtoList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dtoList);
    }

    // Guardar una comuna
    @PostMapping
    public ResponseEntity<ComunaDTO> guardar(@RequestBody @Valid ComunaDTORequest request) {
        
        Comuna comuna = comunaService.guardar(request);

        if(comuna == null){
            return ResponseEntity.badRequest().build();
        }

        ComunaDTO dto = cMapper.toDTO(comuna);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Editar una comuna
    @PutMapping("/{id}")
    public ResponseEntity<ComunaDTO> editar(@PathVariable Long id, @RequestBody @Valid ComunaDTORequest request) {
        
        Comuna comuna = comunaService.editar(id, request);
        
        if(comuna == null){
            return ResponseEntity.badRequest().build();
        }

        ComunaDTO dto = cMapper.toDTO(comuna);
        return ResponseEntity.ok(dto);
    }

    // Borrar una comuna
    @DeleteMapping("/{id}")
    public ResponseEntity<ComunaDTO> delete(@PathVariable Long id) {
        
        if(comunaService.delete(id)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.notFound().build();
    }
}