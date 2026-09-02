package com.esam.esam_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    private String pNombre;
    private String sNombre;
    private String aPaterno;
    private String aMaterno;

    private String nombreUsuario;
    private String password;

    // Muchos usuarios pueden tener un rol
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idRolUsuario")
    private RolUsuario rolUsuario;

    // Muchos usuarios pueden pertenecer a una comuna
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idComuna")
    private Comuna comuna;

    // Muchos usuarios pueden pertenecer a una region
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idRegion")
    private Region region;
}
