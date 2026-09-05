package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse;
import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse.ComunaDTO;
import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse.RegionDTO;
import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse.RolDTO;
import com.esam.esam_backend.model.Usuario;

public class UsuarioMapper {

    public UsuarioDTOResponse toDTO(Usuario usuario) {
        UsuarioDTOResponse dto = new UsuarioDTOResponse();
        dto.setId(usuario.getIdUsuario());
        dto.setNombres(usuario.getNombres());
        dto.setAPaterno(usuario.getAPaterno());
        dto.setAMaterno(usuario.getAMaterno());
        dto.setRut(usuario.getRut());
        dto.setDv(usuario.getDv());
        dto.setCorreo(usuario.getCorreo());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setDireccion(usuario.getDireccion());
        dto.setTelefono(usuario.getTelefono());
        RegionDTO region = new RegionDTO();
        region.setIdRegion(usuario.getRegion().getIdRegion());
        region.setNombre(usuario.getRegion().getNombre());
        dto.setRegion(region);
        ComunaDTO comuna = new ComunaDTO();
        comuna.setIdComuna(usuario.getComuna().getIdComuna());
        comuna.setNombre(usuario.getComuna().getNombre());
        dto.setComuna(comuna);
        RolDTO rol = new RolDTO();
        rol.setIdRolUsuario(usuario.getRolUsuario().getIdRolUsuario());
        rol.setNombre(usuario.getRolUsuario().getNombre());
        dto.setRol(rol);
        dto.setNombreUsuario(usuario.getNombreUsuario());
        return dto;
    }

    public List<UsuarioDTOResponse> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
