package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.rolUsuario.RolUsuarioDTO;
import com.esam.esam_backend.dto.rolUsuario.RolUsuarioDTORequest;
import com.esam.esam_backend.model.RolUsuario;

@Component 
public class RolUsuarioMapper {

    public RolUsuarioDTO toDTO(RolUsuario rol) {
        RolUsuarioDTO dto = new RolUsuarioDTO();
        dto.setIdRolUsuario(rol.getIdRolUsuario());
        dto.setNombre(rol.getNombre());
        return dto;
    }

    public List<RolUsuarioDTO> toDTOList(List<RolUsuario> roles) {
        return roles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RolUsuario toEntity(RolUsuarioDTORequest request) {
        RolUsuario rol = new RolUsuario();
        rol.setNombre(request.getNombre());
        return rol;
    }
}
