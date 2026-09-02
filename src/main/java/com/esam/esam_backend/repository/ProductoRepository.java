package com.esam.esam_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esam.esam_backend.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{

    // Productos según su marca
    List<Producto> findByMarcaIdMarca(Long idMarca);

    // Productos según rango de precio
    List<Producto> findByPrecioBetween(Long precioMin, Long precioMax);

    // Productos según rango de stock
    List<Producto> findByStockBetween(Long stockMin, Long stockMax);

    // Productos según su nombre
    List<Producto> findByNombre(String nombre);
}