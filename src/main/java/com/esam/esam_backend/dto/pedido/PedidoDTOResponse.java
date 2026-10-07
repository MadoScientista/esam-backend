package com.esam.esam_backend.dto.pedido;

import java.time.Instant;
import java.util.List;

import com.esam.esam_backend.enums.EstadoPedido;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoDTOResponse {

    private Long idPedido;
    private String numeroPedido;
    private EstadoPedido estado;
    private String tipoEntrega;
    private Long total;
    private Instant creadoEn;
    private DatosEnvioDTOResponse envio;
    private List<DetallePedidoDTOResponse> detalles;
    private List<CambioEstadoPedidoDTOResponse> historialEstados;
}
