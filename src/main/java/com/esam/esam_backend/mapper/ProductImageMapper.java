package com.esam.esam_backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.productImage.ProductImageResponse;
import com.esam.esam_backend.model.ProductImage;
import com.esam.esam_backend.model.Producto;

@Component
public class ProductImageMapper {

    public ProductImageResponse toDTO(ProductImage entity) {
        ProductImageResponse dto = new ProductImageResponse();
        dto.setIdProductImage(entity.getIdProductImage());
        dto.setUrl(entity.getUrl());
        dto.setPrincipal(entity.isPrincipal());
        return dto;
    }

    public List<ProductImageResponse> toDTOList(List<ProductImage> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public ProductImage toEntity(String url, String publicId, Producto producto, boolean principal) {
        ProductImage entity = new ProductImage();
        entity.setUrl(url);
        entity.setPublicId(publicId);
        entity.setProducto(producto);
        entity.setPrincipal(principal);
        return entity;
    }
}
