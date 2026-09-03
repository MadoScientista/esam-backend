package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.esam.esam_backend.dto.usuario.UsuarioDTOResponse;
import com.esam.esam_backend.model.Usuario;

public class UsuarioMapper {

    public UsuarioDTOResponse toDTO(Usuario usuario) {
        UsuarioDTOResponse dto = new UsuarioDTOResponse();
        dto.setId(usuario.getIdUsuario());
        dto.setNombres(usuario.getNombres());
        dto.setAPaterno(usuario.getAPaterno());
        dto.setAMaterno(usuario.getAMaterno());
        dto.setCorreo(usuario.getCorreo());
        dto.setRegion(usuario.getRegion().getNombre());
        dto.setComuna(usuario.getComuna().getNombre());
        dto.setRol(usuario.getRolUsuario().getNombre());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        return dto;
    }

    public List<UsuarioDTOResponse> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
