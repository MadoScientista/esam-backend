package com.esam.esam_backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sku;
    
    @Column(nullable = false, length = 50)
    private String nombre;

    @Lob
    @Column(nullable = false)
    private String descripcion;
    
    @Column(nullable = false)
    private Long precio;
    
    @Column(nullable = false)
    private Long stock;
    
    // Un producto puede tener muchas imágenes
    // Se inicializa vacía para que un Producto recién creado no devuelva null
    @OneToMany(mappedBy = "producto")
    private List<ImagenProducto> imagenes = new ArrayList<>();
    
    // Varios productos pueden tener una marca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMarca")
    private Marca marca;
}
