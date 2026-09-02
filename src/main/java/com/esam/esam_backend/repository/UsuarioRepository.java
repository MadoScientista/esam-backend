package com.esam.esam_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esam.esam_backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    List<Usuario> findByRolUsuarioIdRolUsuario(Long idRolUsuario);

    Usuario findByNombreUsuario(String nombreUsuario);
}
