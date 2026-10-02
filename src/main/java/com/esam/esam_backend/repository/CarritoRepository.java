package com.esam.esam_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esam.esam_backend.model.Carrito;

import jakarta.persistence.LockModeType;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuario_IdUsuario(Long idUsuario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select c from Carrito c
            where c.usuario.idUsuario = :idUsuario
            """)
    Optional<Carrito> buscarParaPedidoPorUsuario(@Param("idUsuario") Long idUsuario);
}
