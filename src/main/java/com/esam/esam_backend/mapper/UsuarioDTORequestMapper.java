package com.esam.esam_backend.mapper;

import com.esam.esam_backend.dto.usuario.UsuarioDTORequest;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;

public class UsuarioDTORequestMapper {

    public Usuario toEntity(UsuarioDTORequest dto, RolUsuario rol, Region region, Comuna comuna) {
        Usuario usuario = new Usuario();
        usuario.setNombres(dto.getNombres());
        usuario.setAPaterno(dto.getAPaterno());
        usuario.setAMaterno(dto.getAMaterno());
        usuario.setRut(dto.getRut());
        usuario.setDv(dto.getDv());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setDireccion(dto.getDireccion());
        usuario.setTelefono(dto.getTelefono());
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(dto.getPassword());
        usuario.setRolUsuario(rol);
        usuario.setRegion(region);
        usuario.setComuna(comuna);
        return usuario;
    }

    public UsuarioDTORequest toDTO(Usuario usuario) {
        UsuarioDTORequest dto = new UsuarioDTORequest();
        dto.setNombres(usuario.getNombres());
        dto.setAPaterno(usuario.getAPaterno());
        dto.setAMaterno(usuario.getAMaterno());
        dto.setRut(usuario.getRut());
        dto.setDv(usuario.getDv());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setDireccion(usuario.getDireccion());
        dto.setTelefono(usuario.getTelefono());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setCorreo(usuario.getCorreo());
        dto.setPassword(usuario.getPassword());
        dto.setIdRolUsuario(usuario.getRolUsuario().getIdRolUsuario());
        dto.setIdRegion(usuario.getRegion().getIdRegion());
        dto.setIdComuna(usuario.getComuna().getIdComuna());
        return dto;
    }
}