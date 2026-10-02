package com.esam.esam_backend.dto.pedido;

import com.esam.esam_backend.enums.EstadoPedido;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoEstadoDTORequest {

    @NotNull
    private EstadoPedido estado;
}
