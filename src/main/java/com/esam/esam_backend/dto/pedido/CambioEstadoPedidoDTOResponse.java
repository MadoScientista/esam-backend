package com.esam.esam_backend.dto.pedido;

import java.time.Instant;

import com.esam.esam_backend.enums.EstadoPedido;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CambioEstadoPedidoDTOResponse {

    private EstadoPedido estadoAnterior;
    private EstadoPedido estadoNuevo;
    private Instant cambiadoEn;
}
