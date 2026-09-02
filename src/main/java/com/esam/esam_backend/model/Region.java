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
@Data
@NoArgsConstructor
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegion;
    private String nombre;

    //One to Many
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<Comuna> comunas = new ArrayList<>();

    //Una region puede tener muchos usuarios
    @OneToMany(mappedBy = "region")
    private List<Usuario> usuarios = new ArrayList<>();
}
