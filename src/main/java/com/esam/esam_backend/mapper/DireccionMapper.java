package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.direccion.DireccionDTO;
import com.esam.esam_backend.dto.direccion.DireccionDTORequest;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.model.Usuario;

@Component
public class DireccionMapper {

    public DireccionDTO toDTO(Direccion direccion) {
        DireccionDTO dto = new DireccionDTO();
        dto.setIdDireccion(direccion.getIdDireccion());
        dto.setNombreReceptor(direccion.getNombreReceptor());
        dto.setTelefonoReceptor(direccion.getTelefonoReceptor());
        dto.setCalle(direccion.getCalle());
        dto.setNumero(direccion.getNumero());
        dto.setComplemento(direccion.getComplemento());
        dto.setPredeterminada(direccion.getPredeterminada());
        dto.setActivo(direccion.getActivo());
        dto.setIdUsuario(direccion.getUsuario() != null ? direccion.getUsuario().getIdUsuario() : null);
        dto.setIdComuna(direccion.getComuna() != null ? direccion.getComuna().getIdComuna() : null);
        return dto;
    }

    public List<DireccionDTO> toDTOList(List<Direccion> direcciones) {
        return direcciones.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Direccion toEntity(DireccionDTORequest request, Usuario usuario, Comuna comuna) {
        Direccion direccion = new Direccion();
        direccion.setNombreReceptor(request.getNombreReceptor());
        direccion.setTelefonoReceptor(request.getTelefonoReceptor());
        direccion.setCalle(request.getCalle());
        direccion.setNumero(request.getNumero());
        direccion.setComplemento(request.getComplemento());
        direccion.setPredeterminada(Boolean.TRUE.equals(request.getPredeterminada()));
        direccion.setActivo(true);
        direccion.setUsuario(usuario);
        direccion.setComuna(comuna);
        return direccion;
    }
}
