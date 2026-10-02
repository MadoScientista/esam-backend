package com.esam.esam_backend.dto.pedido;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DetallePedidoDTOResponse {

    private Long idDetallePedido;
    private Long idProducto;
    private String nombreProducto;
    private String skuProducto;
    private Long precioUnitario;
    private Integer cantidad;
    private Long subtotal;
}
