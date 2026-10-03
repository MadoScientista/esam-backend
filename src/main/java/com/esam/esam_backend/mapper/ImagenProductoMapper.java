package com.esam.esam_backend.mapper;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.imagenProducto.ImagenProductoResponse;
import com.esam.esam_backend.model.ImagenProducto;
import com.esam.esam_backend.model.Producto;

@Component
public class ImagenProductoMapper {

    public ImagenProductoResponse toDTO(ImagenProducto entity) {
        ImagenProductoResponse dto = new ImagenProductoResponse();
        dto.setIdImagenProducto(entity.getIdImagenProducto());
        dto.setUrl(entity.getUrl());
        dto.setTextoAlternativo(entity.getTextoAlternativo());
        dto.setOrden(entity.getOrden());
        dto.setPrincipal(entity.getPrincipal());
        return dto;
    }

    public List<ImagenProductoResponse> toDTOList(List<ImagenProducto> entities) {
        return entities.stream()
                .sorted(Comparator.comparing(ImagenProducto::getOrden)
                        .thenComparing(ImagenProducto::getIdImagenProducto))
                .map(this::toDTO)
                .toList();
    }

    public ImagenProductoResponse toImagenPrincipalDTO(Producto producto) {
        return producto.getImagenes().stream()
                .filter(imagen -> Boolean.TRUE.equals(imagen.getPrincipal()))
                .min(Comparator.comparing(ImagenProducto::getOrden)
                        .thenComparing(ImagenProducto::getIdImagenProducto))
                .map(this::toDTO)
                .orElse(null);
    }

    public ImagenProducto toEntity(String url, String idPublico, Producto producto, Integer orden) {
        ImagenProducto entity = new ImagenProducto();
        entity.setUrl(url);
        entity.setIdPublico(idPublico);
        entity.setProducto(producto);
        entity.setOrden(orden);
        return entity;
    }
}
