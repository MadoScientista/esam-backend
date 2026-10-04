package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.region.RegionComunasDTO;
import com.esam.esam_backend.dto.region.RegionDTO;
import com.esam.esam_backend.dto.region.RegionDTORequest;
import com.esam.esam_backend.model.Region;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Component 
public class RegionMapper {

    private final ComunaMapper comunaMapper;

    public RegionDTO toDTO(Region region) {
        RegionDTO dto = new RegionDTO();
        dto.setIdRegion(region.getIdRegion());
        dto.setNombre(region.getNombre());
        return dto;
    }

    public List<RegionDTO> toDTOList(List<Region> regiones) {
        return regiones.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RegionComunasDTO toConComunasDTO(Region region) {
        RegionComunasDTO dto = new RegionComunasDTO();
        dto.setIdRegion(region.getIdRegion());
        dto.setNombre(region.getNombre());
        dto.setComunas(comunaMapper.toDTOList(region.getComunas()));
        return dto;
    }

    public List<RegionComunasDTO> toConComunasDTOList(List<Region> regiones) {
        return regiones.stream()
                .map(this::toConComunasDTO)
                .collect(Collectors.toList());
    }

    public Region toEntity(RegionDTORequest request) {
        Region region = new Region();
        region.setNombre(request.getNombre());
        return region;
    }
}
