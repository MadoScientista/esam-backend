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
public class ImagenProducto {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImagenProducto;

    // URL pública de la imagen
    @Column(nullable = false, length = 500)
    private String url;

    // Identificador de Cloudinary necesario para borrar la imagen
    @Column(nullable = false, unique = true)
    private String idPublico;

    // Posición dentro de la galería del producto. 0 es la primera.
    // El listado ordena por orden y, a igualdad, por idImagenProducto.
    @Column(nullable = false)
    private Integer orden;


    // Relación muchas imágenes a un producto
    @ManyToOne(fetch = FetchType.LAZY, optional=false)
    @JoinColumn(name="sku", nullable = false)
    private Producto producto;
}
