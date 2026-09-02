package com.esam.esam_backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMarca;
    private String nombre;

    //Una marca puede tener muchos productos
    @OneToMany(mappedBy="marca", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<Producto> productos = new ArrayList<>();

    
}
