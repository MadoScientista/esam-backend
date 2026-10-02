package com.esam.esam_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_carrito", uniqueConstraints = @UniqueConstraint(
        name = "uk_item_carrito_carrito_producto",
        columnNames = { "id_carrito", "id_producto" }))
@Getter
@Setter
@NoArgsConstructor
public class ItemCarrito{

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemCarrito;

    @Column(nullable = false)
    private Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_carrito", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
}
