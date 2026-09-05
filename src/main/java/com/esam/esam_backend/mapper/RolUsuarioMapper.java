package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.esam.esam_backend.dto.rolusuario.RolUsuarioDTOResponse;
import com.esam.esam_backend.model.RolUsuario;

public class RolUsuarioMapper {

    public RolUsuarioDTOResponse toDTO(RolUsuario rol) {
        RolUsuarioDTOResponse dto = new RolUsuarioDTOResponse();
        dto.setIdRolUsuario(rol.getIdRolUsuario());
        dto.setNombre(rol.getNombre());
        return dto;
    }

    public List<RolUsuarioDTOResponse> toDTOList(List<RolUsuario> roles) {
        return roles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
