package com.esam.esam_backend.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.esam.esam_backend.enums.EstadoPedido;
import com.esam.esam_backend.enums.TipoEntrega;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Setter
@NoArgsConstructor
public class Pedido{


    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @Column(nullable = false, unique = true)
    private String numeroPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEntrega tipoEntrega;

    @Column(nullable = false)
    private Long total;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    private String nombreReceptor;

    private String telefonoReceptor;

    private String calle;

    private String numero;

    @Column
    private String complemento;

    private String comunaNombre;

    private String regionNombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetallePedido> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CambioEstadoPedido> historialEstados = new ArrayList<>();
}
