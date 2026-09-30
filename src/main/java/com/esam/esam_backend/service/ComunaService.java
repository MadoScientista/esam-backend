package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.comuna.ComunaDTORequest;
import com.esam.esam_backend.exception.ComunaConUsuariosException;
import com.esam.esam_backend.exception.ComunaInvalidaException;
import com.esam.esam_backend.exception.ComunaNoEncontradaException;
import com.esam.esam_backend.mapper.ComunaMapper;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.RegionRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ComunaService {

    private final ComunaRepository cRepo;

    private final RegionRepository rRepo;

    private final UsuarioRepository uRepo;

    private final ComunaMapper cMapper;

    public Comuna obtenerPorId(Long id) {
        return cRepo.findById(id).orElse(null);
    }

    public List<Comuna> obtenerTodos() {
        return cRepo.findAll();
    }

    // Guardar
    public Comuna guardar(ComunaDTORequest request) {
        Region region = obtenerRegion(request.getIdRegion());
        return cRepo.save(cMapper.toEntity(request, region));
    }

    // Editar
    public Comuna editar(Long id, ComunaDTORequest request) {
        Comuna comuna = obtenerComuna(id);
        Region region = obtenerRegion(request.getIdRegion());

        comuna.setNombre(request.getNombre());
        comuna.setRegion(region);

        return cRepo.save(comuna);
    }

    // Borrar
    public void borrar(Long id) {
        Comuna comuna = obtenerComuna(id);

        long usuariosAsociados = uRepo.countByComunaIdComuna(id);

        if (usuariosAsociados > 0) {
            throw new ComunaConUsuariosException(
                    "La comuna " + id + " tiene " + usuariosAsociados + " usuario(s) asociado(s)");
        }

        cRepo.delete(comuna);
    }

    private Comuna obtenerComuna(Long id) {
        Comuna comuna = obtenerPorId(id);
        if (comuna == null) {
            throw new ComunaNoEncontradaException("No existe una comuna con id " + id);
        }
        return comuna;
    }

    private Region obtenerRegion(Long idRegion) {
        return rRepo.findById(idRegion)
                .orElseThrow(() -> new ComunaInvalidaException("No existe una región con id " + idRegion));
    }
}