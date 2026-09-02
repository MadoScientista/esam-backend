package com.esam.esam_backend.mapper;

import com.esam.esam_backend.dto.usuario.UsuarioDTORequest;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;

public class UsuarioDTORequestMapper {

    public Usuario toEntity(UsuarioDTORequest dto, RolUsuario rol, Region region, Comuna comuna) {
        Usuario usuario = new Usuario();
        usuario.setPNombre(dto.getPNombre());
        usuario.setSNombre(dto.getSNombre());
        usuario.setAPaterno(dto.getAPaterno());
        usuario.setAMaterno(dto.getAMaterno());
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setPassword(dto.getPassword());
        usuario.setRolUsuario(rol);
        usuario.setRegion(region);
        usuario.setComuna(comuna);
        return usuario;
    }

    public UsuarioDTORequest toDTO(Usuario usuario) {
        UsuarioDTORequest dto = new UsuarioDTORequest();
        dto.setPNombre(usuario.getPNombre());
        dto.setSNombre(usuario.getSNombre());
        dto.setAPaterno(usuario.getAPaterno());
        dto.setAMaterno(usuario.getAMaterno());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setPassword(usuario.getPassword());
        dto.setIdRol(usuario.getRolUsuario().getIdRolUsuario());
        dto.setIdRegion(usuario.getRegion().getIdRegion());
        dto.setIdComuna(usuario.getComuna().getIdComuna());
        return dto;
    }
}