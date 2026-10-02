package com.esam.esam_backend.dto.pedido;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PedidoAdminDTOResponse extends PedidoDTOResponse {

    private Long idUsuario;
    private String emailUsuario;
}
