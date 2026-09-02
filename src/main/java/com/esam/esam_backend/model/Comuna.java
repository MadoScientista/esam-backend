package com.esam.esam_backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComuna;
    private String nombre;

    // Relación muchos a uno
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idRegion")
    private Region region;

    // Una comuna puede tener muchos usuarios
    @OneToMany(mappedBy = "comuna")
    private List<Usuario> usuarios = new ArrayList<>();

}
