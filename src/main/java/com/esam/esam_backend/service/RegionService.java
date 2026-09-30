package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.region.RegionDTORequest;
import com.esam.esam_backend.exception.RegionConDependenciasException;
import com.esam.esam_backend.exception.RegionNoEncontradaException;
import com.esam.esam_backend.mapper.RegionMapper;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.RegionRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

@Service
public class RegionService {

    @Autowired
    private RegionRepository rRepo;

    @Autowired
    private ComunaRepository cRepo;

    @Autowired
    private UsuarioRepository uRepo;

    @Autowired
    private RegionMapper rMapper;

    // Obtener según su id
    public Region obtenerPorId(Long id) {
        return rRepo.findById(id).orElse(null);
    }

    // Obtener todos
    public List<Region> obtenerTodos() {
        return rRepo.findAll();
    }

    // Guardar
    public Region guardar(RegionDTORequest request) {
        return rRepo.save(rMapper.toEntity(request));
    }

    // Editar
    public Region editar(Long id, RegionDTORequest request) {
        Region region = obtenerRegion(id);

        region.setNombre(request.getNombre());

        return rRepo.save(region);
    }

    // Borrar
    public void borrar(Long id) {
        Region region = obtenerRegion(id);

        long comunasAsociadas = cRepo.countByRegionIdRegion(id);
        long usuariosAsociados = uRepo.countByRegionIdRegion(id);

        if(comunasAsociadas > 0 || usuariosAsociados > 0){
            throw new RegionConDependenciasException(
                    "La región " + id + " tiene " + comunasAsociadas + " comuna(s) y "
                            + usuariosAsociados + " usuario(s) asociado(s)");
        }

        rRepo.delete(region);
    }

    // Todas las regiones con sus comunas
    public List<Region> obtenerTodasConComunas() {
        return rRepo.findAllConComunas();
    }

    // Una región con sus comunas
    public Region obtenerPorIdConComunas(Long id) {
        return rRepo.findByIdConComunas(id).orElse(null);
    }

    private Region obtenerRegion(Long id) {
        Region region = obtenerPorId(id);

        if(region == null){
            throw new RegionNoEncontradaException("No existe una región con id " + id);
        }

        return region;
    }
}
