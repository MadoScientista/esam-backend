package com.esam.esam_backend.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.comuna.ComunaDTORequest;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.RegionRepository;

@Service
public class ComunaService {

    @Autowired
    private ComunaRepository cRepo;

    @Autowired 
    private RegionRepository rRepo;
    

    // Obtener según su id
    public Comuna obtenerPorId(Long id) {
        return cRepo.findById(id).orElse(null);
    }

    // Obtener todos
    public List<Comuna> obtenerTodos() {
        return cRepo.findAll();
    }

    // Guardar
    public Comuna guardar(Comuna comuna) {
        return cRepo.save(comuna);
    }

    public Comuna guardar(ComunaDTORequest request) {
        if(rRepo.existsById(request.getIdRegion())){

            Comuna comuna = new Comuna();
            comuna.setRegion(rRepo.findById(request.getIdRegion()).get());
            return cRepo.save(comuna);
        }

        return null;
    }

    // Editar
    public Comuna editar(Long id, Comuna datos) {
        Comuna comuna = obtenerPorId(id);
        comuna.setNombre(datos.getNombre());
        comuna.setRegion(datos.getRegion());
        return cRepo.save(comuna);
    }

    public Comuna editar(Long id, ComunaDTORequest request){
        Comuna comuna = obtenerPorId(id);
        Region region = rRepo.findById(request.getIdRegion()).orElse(null);

        if(region == null){
            return null;
        }

        comuna.setNombre(request.getNombre());
        comuna.setRegion(region);

        return cRepo.save(comuna);
    }

    // Borrar
    public boolean delete(Long id) {
        Comuna comuna = obtenerPorId(id);
        
        if(comuna == null){
            return false;
        }
        
        cRepo.delete(comuna);
        return true;
    }
}
