package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.repository.MarcaRepository;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository mRepo;

    // Obtener según su id
    public Marca obtenerPorId(Long id) {
        return mRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada con id: " + id));
    }

    // Obtener todos
    public List<Marca> obtenerTodos() {
        return mRepo.findAll();
    }

    // Guardar
    public Marca guardar(Marca marca) {
        return mRepo.save(marca);
    }

    // Editar
    public Marca editar(Long id, Marca datos) {
        Marca marca = obtenerPorId(id);
        marca.setNombre(datos.getNombre());
        return mRepo.save(marca);
    }

    // Borrar
    public void borrar(Long id) {
        Marca marca = obtenerPorId(id);
        mRepo.delete(marca);
    }
}
