package com.esam.esam_backend.model;

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
    
    private String nombre;

    @Lob
    private String descripcion;
    private Long precio;
    private Long stock;
    private String img;
    
    // Varios productos pueden tener una marca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idMarca")
    private Marca marca;
}
