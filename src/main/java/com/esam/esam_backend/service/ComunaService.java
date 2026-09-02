package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.repository.ComunaRepository;

@Service
public class ComunaService {

    @Autowired
    private ComunaRepository cRepo;

    // Obtener según su id
    public Comuna obtenerPorId(Long id) {
        return cRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Comuna no encontrada con id: " + id));
    }

    // Obtener todos
    public List<Comuna> obtenerTodos() {
        return cRepo.findAll();
    }

    // Guardar
    public Comuna guardar(Comuna comuna) {
        return cRepo.save(comuna);
    }

    // Editar
    public Comuna editar(Long id, Comuna datos) {
        Comuna comuna = obtenerPorId(id);
        comuna.setNombre(datos.getNombre());
        comuna.setRegion(datos.getRegion());
        return cRepo.save(comuna);
    }

    // Borrar
    public void borrar(Long id) {
        Comuna comuna = obtenerPorId(id);
        cRepo.delete(comuna);
    }
}
