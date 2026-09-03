package com.esam.esam_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.esam.esam_backend.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long>{

    @Query("select distinct r from Region r left join fetch r.comunas")
    List<Region> findAllConComunas();

    @Query("select r from Region r left join fetch r.comunas where r.idRegion = :id")
    java.util.Optional<Region> findByIdConComunas(Long id);
}
