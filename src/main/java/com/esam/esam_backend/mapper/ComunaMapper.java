package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.comuna.ComunaDTO;
import com.esam.esam_backend.model.Comuna;

@Component 
public class ComunaMapper {

    public ComunaDTO toDTO(Comuna comuna) {
        ComunaDTO dto = new ComunaDTO();
        dto.setIdComuna(comuna.getIdComuna());
        dto.setNombre(comuna.getNombre());
        return dto;
    }

    public List<ComunaDTO> toDTOList(List<Comuna> comunas) {
        return comunas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}