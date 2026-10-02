package com.esam.esam_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esam.esam_backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    List<Usuario> findByRolUsuarioIdRolUsuario(Long idRolUsuario);

    Optional<Usuario> findByCorreo(String correo);

    // Cantidad de usuarios asociados a una comuna
    long countByComunaIdComuna(Long idComuna);

    // Cantidad de usuarios asociados a un rol
    long countByRolUsuarioIdRolUsuario(Long idRolUsuario);
}