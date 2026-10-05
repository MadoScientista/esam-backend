package com.esam.esam_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esam.esam_backend.enums.EstadoPedido;
import com.esam.esam_backend.model.Pedido;

import jakarta.persistence.LockModeType;

public interface PedidoRepository extends JpaRepository<Pedido, Long>{

    List<Pedido> findByUsuario_IdUsuarioOrderByCreadoEnDescIdPedidoDesc(Long idUsuario);

    Optional<Pedido> findByIdPedidoAndUsuario_IdUsuario(Long idPedido, Long idUsuario);

    List<Pedido> findAllByOrderByCreadoEnDescIdPedidoDesc();

    long countByEstado(EstadoPedido estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.idPedido = :idPedido")
    Optional<Pedido> buscarPorIdParaActualizar(@Param("idPedido") Long idPedido);
}
