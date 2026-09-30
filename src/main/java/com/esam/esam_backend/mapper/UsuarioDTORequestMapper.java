package com.esam.esam_backend.mapper;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.usuario.UsuarioDTORequest;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;

@Component
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
        usuario.setCorreo(dto.getCorreo());
        usuario.setRolUsuario(rol);
        usuario.setRegion(region);
        usuario.setComuna(comuna);
        return usuario;
    }
}