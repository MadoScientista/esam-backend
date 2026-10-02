package com.esam.esam_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esam.esam_backend.model.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Long>{

    List<Direccion> findByUsuarioIdUsuario(Long idUsuario);

    List<Direccion> findByUsuarioIdUsuarioAndActivoTrueOrderByIdDireccionAsc(Long idUsuario);

    Optional<Direccion> findByIdDireccionAndUsuarioIdUsuarioAndActivoTrue(
            Long idDireccion, Long idUsuario);

    long countByUsuarioIdUsuario(Long idUsuario);

    long countByComunaIdComuna(Long idComuna);
}
