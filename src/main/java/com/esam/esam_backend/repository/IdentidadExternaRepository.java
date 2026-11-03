package com.esam.esam_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esam.esam_backend.model.IdentidadExterna;

public interface IdentidadExternaRepository extends JpaRepository<IdentidadExterna, Long> {

    Optional<IdentidadExterna> findByProveedorAndSubject(String proveedor, String subject);
}
