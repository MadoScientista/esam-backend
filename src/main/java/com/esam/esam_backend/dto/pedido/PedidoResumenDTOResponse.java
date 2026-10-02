package com.esam.esam_backend.dto.pedido;

import java.time.Instant;

import com.esam.esam_backend.enums.EstadoPedido;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoResumenDTOResponse {

    private Long idPedido;
    private String numeroPedido;
    private EstadoPedido estado;
    private Long total;
    private Integer cantidadItems;
    private Instant creadoEn;
}
