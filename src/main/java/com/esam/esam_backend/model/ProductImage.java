package com.esam.esam_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Data 
@NoArgsConstructor 
public class ProductImage {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProductImage;

    // URL pública de la imagen
    @Column(nullable = false, length = 500)
    private String url;

    // Identificador de Cloudinary necesario para borrar la imagen
    @Column(nullable = false, unique = true)
    private String publicId;

    // Solo una imagen por producto puede ser la principal.
    // La garantía de unicidad la impone el índice UK_product_image_principal
    // sobre la columna generada principal_key.
    @Column(nullable = false)
    private boolean principal;


    // Relación muchas imágenes a un producto
    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="sku", nullable = false)
    private Producto producto;
}
