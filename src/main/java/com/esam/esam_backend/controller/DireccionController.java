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

import com.esam.esam_backend.dto.direccion.DireccionDTO;
import com.esam.esam_backend.dto.direccion.DireccionDTORequest;
import com.esam.esam_backend.mapper.DireccionMapper;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.service.DireccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/direcciones")
public class DireccionController {

    private final DireccionService direccionService;
    private final DireccionMapper dMapper;

    @GetMapping
    public ResponseEntity<List<DireccionDTO>> obtenerTodos() {
        return ResponseEntity.ok(dMapper.toDTOList(direccionService.obtenerTodos()));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<DireccionDTO>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(dMapper.toDTOList(direccionService.obtenerPorUsuario(idUsuario)));
    }

    @GetMapping("/usuario/{idUsuario}/activas")
    public ResponseEntity<List<DireccionDTO>> obtenerActivasPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(dMapper.toDTOList(direccionService.obtenerActivasPorUsuario(idUsuario)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionDTO> obtenerPorId(@PathVariable Long id) {
        Direccion direccion = direccionService.obtenerPorId(id);
        if (direccion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dMapper.toDTO(direccion));
    }

    @PostMapping
    public ResponseEntity<DireccionDTO> guardar(@RequestBody @Valid DireccionDTORequest request) {
        Direccion direccion = direccionService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dMapper.toDTO(direccion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionDTO> editar(@PathVariable Long id, @RequestBody @Valid DireccionDTORequest request) {
        Direccion direccion = direccionService.editar(id, request);
        return ResponseEntity.ok(dMapper.toDTO(direccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        direccionService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
