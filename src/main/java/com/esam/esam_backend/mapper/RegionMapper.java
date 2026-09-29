package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.region.RegionComunasDTO;
import com.esam.esam_backend.model.Region;

@Component 
public class RegionMapper {

    private final ComunaMapper comunaMapper = new ComunaMapper();

    public RegionComunasDTO toDTO(Region region) {
        RegionComunasDTO dto = new RegionComunasDTO();
        dto.setIdRegion(region.getIdRegion());
        dto.setRegion(region.getNombre());
        dto.setComunas(comunaMapper.toDTOList(region.getComunas()));
        return dto;
    }

    public List<RegionComunasDTO> toDTOList(List<Region> regiones) {
        return regiones.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}