package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.categoria.CategoriaDTO;
import com.esam.esam_backend.dto.categoria.CategoriaDTORequest;
import com.esam.esam_backend.model.Categoria;

@Component
public class CategoriaMapper {

    public CategoriaDTO toDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(categoria.getIdCategoria());
        dto.setNombre(categoria.getNombre());
        dto.setSlug(categoria.getSlug());
        dto.setIdCategoriaPadre(categoria.getPadre() != null ? categoria.getPadre().getIdCategoria() : null);
        return dto;
    }

    public List<CategoriaDTO> toDTOList(List<Categoria> categorias) {
        return categorias.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Categoria toEntity(CategoriaDTORequest request, Categoria padre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setPadre(padre);
        return categoria;
    }
}