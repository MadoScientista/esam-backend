package com.esam.esam_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esam.esam_backend.model.ImagenProducto;

public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long>{

    // Imágenes de un producto, la portada primero y luego por posición.
    // Es un @Query y no un método derivado porque el nombre de ordenación sería
    // ilegible. El desempate final por id mantiene un orden estable cuando dos
    // imágenes comparten posición.

    List<ImagenProducto> findByProductoSkuOrderByOrdenAsc(Long sku);

    // Cantidad de imágenes de un producto
    long countByProductoSku(Long sku);

    // Última posición ocupada en la galería del producto, o -1 si no tiene imágenes.
    // Sirve para que una imagen nueva se agregue al final en lugar de empezar en 0.
    @Query("select coalesce(max(i.orden), -1) from ImagenProducto i where i.producto.sku = :sku")
    Integer maxOrdenPorProducto(@Param("sku") Long sku);
}