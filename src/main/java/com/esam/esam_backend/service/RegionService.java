package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.region.RegionComunasDTO;
import com.esam.esam_backend.mapper.RegionMapper;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.repository.RegionRepository;

@Service
public class RegionService {

    @Autowired
    private RegionRepository rRepo;

    private final RegionMapper regionMapper = new RegionMapper();

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
        return regionMapper.toDTOList(rRepo.findAllConComunas());
    }

    // Una región con sus comunas
    public RegionComunasDTO obtenerPorIdConComunas(Long id) {
        Region region = rRepo.findByIdConComunas(id)
                .orElseThrow(() -> new RuntimeException("Región no encontrada con id: " + id));
        return regionMapper.toDTO(region);
    }
}
