package com.esam.esam_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esam.esam_backend.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

    long countByPadreIdCategoria(Long idCategoria);

    @Query("""
            select case when count(c) > 0 then true else false end
            from Categoria c
            where c.slug = :slug
              and (:idCategoria is null or c.idCategoria <> :idCategoria)
            """)
    boolean existsBySlugAndIdCategoriaNot(
            @Param("slug") String slug,
            @Param("idCategoria") Long idCategoria);

    @Query("""
            select count(p) from Producto p
            join p.categorias c
            where c.idCategoria = :idCategoria
            """)
    long countProductosAsociados(Long idCategoria);
}
