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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class Comuna{

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComuna;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @OneToMany(mappedBy = "comuna", fetch = FetchType.LAZY)
    private List<Direccion> direcciones = new ArrayList<>();

    @OneToMany(mappedBy = "comuna", fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();
}
