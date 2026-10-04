package com.esam.esam_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.esam.esam_backend.model.Comuna;

public interface ComunaRepository extends JpaRepository<Comuna, Long>{

    @Override
    @EntityGraph(attributePaths = "region")
    List<Comuna> findAll();

    // Cantidad de comunas asociadas a una región
    long countByRegionIdRegion(Long idRegion);
}
