package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.esam.esam_backend.dto.producto.ProductoDTOResponse;
import com.esam.esam_backend.model.Producto;

public class ProductoMapper {

    public ProductoDTOResponse toDTO(Producto producto) {
        ProductoDTOResponse dto = new ProductoDTOResponse();
        dto.setSku(producto.getSku());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setMarca(producto.getMarca().getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        return dto;
    }

    public List<ProductoDTOResponse> toDTOList(List<Producto> productos) {
        return productos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
