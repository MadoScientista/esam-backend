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
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRolUsuario;
    private String nombre;

    // Un rol puede tener muchos usuarios
    @OneToMany(mappedBy = "rolUsuario", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<Usuario> usuarios = new ArrayList<>();
}
