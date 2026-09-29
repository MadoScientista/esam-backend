package com.esam.esam_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
    
    
    private String img;
    
    // Varios productos pueden tener una marca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMarca")
    private Marca marca;
}
