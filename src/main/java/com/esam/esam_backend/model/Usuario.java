package com.esam.esam_backend.model;

import java.time.LocalDate;
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
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Usuario{

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = true, length = 25)
    private String nombres;

    @Column(nullable = true, length = 25)
    private String aPaterno;

    @Column(nullable = true, length = 25)
    private String aMaterno;

    @Column(nullable = true, length = 8)
    private Long rut;

    @Column(nullable = true, length = 1)
    private String dv;

    @Column(nullable = true)
    private LocalDate fechaNacimiento;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol_usuario", nullable = false)
    private RolUsuario rolUsuario;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Direccion> direcciones = new ArrayList<>();

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Carrito carrito;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comuna")
    private Comuna comuna;
}
