package com.esam.esam_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esam.esam_backend.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>{

    // Imágenes de un producto, la principal primero
    List<ProductImage> findByProductoSkuOrderByPrincipalDescIdProductImageAsc(Long sku);

    // Imágenes de un producto que están marcadas como principal
    List<ProductImage> findByProductoSkuAndPrincipalTrue(Long sku);

    // Una imagen concreta que pertenece al producto indicado
    Optional<ProductImage> findByIdProductImageAndProductoSku(Long idProductImage, Long sku);

    // Cantidad de imágenes de un producto
    long countByProductoSku(Long sku);
}
