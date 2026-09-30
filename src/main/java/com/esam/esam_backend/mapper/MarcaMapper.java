package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.marca.MarcaDTO;
import com.esam.esam_backend.dto.marca.MarcaDTORequest;
import com.esam.esam_backend.model.Marca;

@Component 
public class MarcaMapper {

    public MarcaDTO toDTO(Marca marca) {
        MarcaDTO dto = new MarcaDTO();
        dto.setIdMarca(marca.getIdMarca());
        dto.setNombre(marca.getNombre());
        return dto;
    }

    public List<MarcaDTO> toDTOList(List<Marca> marcas) {
        return marcas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Marca toEntity(MarcaDTORequest request) {
        Marca marca = new Marca();
        marca.setNombre(request.getNombre());
        return marca;
    }
}
