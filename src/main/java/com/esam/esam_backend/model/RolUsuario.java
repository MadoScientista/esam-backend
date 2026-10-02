package com.esam.esam_backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class RolUsuario{

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRolUsuario;

    @Column(nullable = false, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "rolUsuario", fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();

}
