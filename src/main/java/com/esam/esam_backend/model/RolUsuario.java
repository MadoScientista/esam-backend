package com.esam.esam_backend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    // Nombres de los roles que la aplicacion necesita para funcionar.
    // No se pueden renombrar ni borrar: el registro publico y las reglas de
    // autorizacion los buscan por nombre.
    public static final String ADMIN = "admin";
    public static final String VENDEDOR = "vendedor";
    public static final String CLIENTE = "cliente";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRolUsuario;

    @Column(nullable = false, length = 25)
    private String nombre;

    // Un rol puede tener muchos usuarios
    @OneToMany(mappedBy = "rolUsuario", cascade = CascadeType.ALL ,orphanRemoval = true)
    private List<Usuario> usuarios = new ArrayList<>();

    public boolean esDelSistema() {
        return ADMIN.equals(nombre) || VENDEDOR.equals(nombre) || CLIENTE.equals(nombre);
    }
}
