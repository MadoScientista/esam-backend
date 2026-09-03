package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.region.RegionComunasDTO;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.repository.RegionRepository;

@Service
public class RegionService {

    @Autowired
    private RegionRepository rRepo;

    // Obtener según su id
    public Region obtenerPorId(Long id) {
        return rRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Región no encontrada con id: " + id));
    }

    // Obtener todos
    public List<Region> obtenerTodos() {
        return rRepo.findAll();
    }

    // Guardar
    public Region guardar(Region region) {
        return rRepo.save(region);
    }

    // Editar
    public Region editar(Long id, Region datos) {
        Region region = obtenerPorId(id);
        region.setNombre(datos.getNombre());
        return rRepo.save(region);
    }

    // Borrar
    public void borrar(Long id) {
        Region region = obtenerPorId(id);
        rRepo.delete(region);
    }

    // Todas las regiones con sus comunas
    public List<RegionComunasDTO> obtenerTodasConComunas() {
        return rRepo.findAllConComunas().stream()
                .map(this::toDTO)
                .toList();
    }

    // Una región con sus comunas
    public RegionComunasDTO obtenerPorIdConComunas(Long id) {
        Region region = rRepo.findByIdConComunas(id)
                .orElseThrow(() -> new RuntimeException("Región no encontrada con id: " + id));
        return toDTO(region);
    }

    // Mapeo de Region a DTO
    private RegionComunasDTO toDTO(Region region) {
        RegionComunasDTO dto = new RegionComunasDTO();
        dto.setIdRegion(region.getIdRegion());
        dto.setRegion(region.getNombre());
        dto.setComunas(region.getComunas().stream()
                .map(this::toComunaDTO)
                .toList());
        return dto;
    }

    // Mapeo de Comuna a DTO
    private RegionComunasDTO.ComunaDTO toComunaDTO(Comuna comuna) {
        RegionComunasDTO.ComunaDTO dto = new RegionComunasDTO.ComunaDTO();
        dto.setIdComuna(comuna.getIdComuna());
        dto.setNombre(comuna.getNombre());
        return dto;
    }
}
