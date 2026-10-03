package com.esam.esam_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.esam.esam_backend.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long>{

    List<DetallePedido> findByPedido_IdPedidoOrderByIdDetallePedidoAsc(Long idPedido);

    @Query("""
            select d from DetallePedido d
            where d.pedido.idPedido in :idsPedido
            order by d.pedido.idPedido, d.idDetallePedido
            """)
    List<DetallePedido> buscarPorPedidos(@Param("idsPedido") List<Long> idsPedido);
}
