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

import com.esam.esam_backend.dto.region.RegionComunasDTO;
import com.esam.esam_backend.dto.region.RegionDTO;
import com.esam.esam_backend.dto.region.RegionDTORequest;
import com.esam.esam_backend.mapper.RegionMapper;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.service.RegionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/regiones")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @Autowired
    private RegionMapper rMapper;

    // Obtener una región por su id
    @GetMapping("/{id}")
    public ResponseEntity<RegionDTO> obtenerPorId(@PathVariable Long id) {

        Region region = regionService.obtenerPorId(id);

        if(region == null){
            return ResponseEntity.notFound().build();
        }

        RegionDTO dto = rMapper.toDTO(region);
        return ResponseEntity.ok(dto);
    }

    // Obtener todas las regiones
    @GetMapping
    public ResponseEntity<List<RegionDTO>> obtenerTodos() {

        List<RegionDTO> dtoList = rMapper.toDTOList(regionService.obtenerTodos());

        return ResponseEntity.ok(dtoList);
    }

    // Obtener todas las regiones con sus comunas
    @GetMapping("/comunas")
    public ResponseEntity<List<RegionComunasDTO>> obtenerTodasConComunas() {

        List<RegionComunasDTO> dtoList = rMapper.toConComunasDTOList(regionService.obtenerTodasConComunas());

        return ResponseEntity.ok(dtoList);
    }

    // Obtener una región con sus comunas
    @GetMapping("/{id}/comunas")
    public ResponseEntity<RegionComunasDTO> obtenerPorIdConComunas(@PathVariable Long id) {

        Region region = regionService.obtenerPorIdConComunas(id);

        if(region == null){
            return ResponseEntity.notFound().build();
        }

        RegionComunasDTO dto = rMapper.toConComunasDTO(region);
        return ResponseEntity.ok(dto);
    }

    // Guardar una región
    @PostMapping
    public ResponseEntity<RegionDTO> guardar(@RequestBody @Valid RegionDTORequest request) {

        Region region = regionService.guardar(request);

        RegionDTO dto = rMapper.toDTO(region);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Editar una región
    @PutMapping("/{id}")
    public ResponseEntity<RegionDTO> editar(@PathVariable Long id, @RequestBody @Valid RegionDTORequest request) {

        Region region = regionService.editar(id, request);

        RegionDTO dto = rMapper.toDTO(region);
        return ResponseEntity.ok(dto);
    }

    // Borrar una región
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {

        regionService.borrar(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
