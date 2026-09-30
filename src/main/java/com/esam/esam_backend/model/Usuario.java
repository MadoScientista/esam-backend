package com.esam.esam_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    // Datos básicos
    private String nombres;
    private String aPaterno;
    private String aMaterno;

    private Long rut;
    private String dv;

    private LocalDate fechaNacimiento;

    private String direccion;
    private Long telefono;

    // Datos de credenciales

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @NotBlank
    @Column(nullable = false)
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
