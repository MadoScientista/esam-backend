package com.esam.esam_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DetallePedido {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetallePedido;

    @Column(nullable = false)
    private String nombreProducto;

    @Column(nullable = false)
    private String skuProducto;

    @Column(nullable = false)
    private Long precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Long subtotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}
