package com.esam.esam_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.esam.esam_backend.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{

    // Todos los productos con sus imagenes y su marca, la principal primero
    @Query("""
            select distinct p from Producto p
            left join fetch p.imagenes i
            left join fetch p.marca
            order by p.sku, i.principal desc, i.idProductImage
            """)
    List<Producto> findAllConImagenes();

    // Un producto con sus imagenes
    @Query("""
            select distinct p from Producto p
            left join fetch p.imagenes i
            left join fetch p.marca
            where p.sku = :sku
            order by i.principal desc, i.idProductImage
            """)
    Optional<Producto> findByIdConImagenes(Long sku);

    // Productos según idMarca
    List<Producto> findByMarcaIdMarca(Long idMarca);

    // Productos según su nombre de marca
    List<Producto> findByMarcaNombre(String nombre);

    // Productos según rango de precio
    List<Producto> findByPrecioBetween(Long precioMin, Long precioMax);

    // Productos según rango de stock
    List<Producto> findByStockBetween(Long stockMin, Long stockMax);

    // Productos según su nombre
    List<Producto> findByNombreContaining(String nombre);

    // Cantidad de productos asociados a una marca
    long countByMarcaIdMarca(Long idMarca);
}
