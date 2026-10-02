package com.esam.esam_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esam.esam_backend.model.RolUsuario;

public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long>{

    // Se usa para resolver el rol "cliente" sin depender de un id fijo
    Optional<RolUsuario> findByNombre(String nombre);

}