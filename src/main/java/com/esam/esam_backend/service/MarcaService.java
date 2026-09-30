package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.marca.MarcaDTORequest;
import com.esam.esam_backend.exception.MarcaConProductosException;
import com.esam.esam_backend.exception.MarcaNoEncontradaException;
import com.esam.esam_backend.mapper.MarcaMapper;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.repository.MarcaRepository;
import com.esam.esam_backend.repository.ProductoRepository;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository mRepo;

    @Autowired
    private ProductoRepository pRepo;

    @Autowired
    private MarcaMapper mMapper;

    // Obtener según su id
    public Marca obtenerPorId(Long id) {
        return mRepo.findById(id).orElse(null);
    }

    // Obtener todos
    public List<Marca> obtenerTodos() {
        return mRepo.findAll();
    }

    // Guardar
    public Marca guardar(MarcaDTORequest request) {
        return mRepo.save(mMapper.toEntity(request));
    }

    // Editar
    public Marca editar(Long id, MarcaDTORequest request) {
        Marca marca = obtenerMarca(id);

        marca.setNombre(request.getNombre());

        return mRepo.save(marca);
    }

    // Borrar
    public void borrar(Long id) {
        Marca marca = obtenerMarca(id);

        long productosAsociados = pRepo.countByMarcaIdMarca(id);

        if(productosAsociados > 0){
            throw new MarcaConProductosException(
                    "La marca " + id + " tiene " + productosAsociados + " producto(s) asociado(s)");
        }

        mRepo.delete(marca);
    }

    // Borrar junto con todos sus productos
    public void borrarEnCascada(Long id) {
        Marca marca = obtenerMarca(id);

        mRepo.delete(marca);
    }

    private Marca obtenerMarca(Long id) {
        Marca marca = obtenerPorId(id);

        if(marca == null){
            throw new MarcaNoEncontradaException("No existe una marca con id " + id);
        }

        return marca;
    }
}
