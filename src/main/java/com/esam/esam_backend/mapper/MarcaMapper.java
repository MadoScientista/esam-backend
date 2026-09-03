package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.esam.esam_backend.dto.marca.MarcaDTO;
import com.esam.esam_backend.model.Marca;

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
}
