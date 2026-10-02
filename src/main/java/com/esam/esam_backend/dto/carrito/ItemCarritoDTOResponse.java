package com.esam.esam_backend.dto.carrito;

import com.esam.esam_backend.dto.producto.ProductoResumenDTOResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemCarritoDTOResponse {

    private Long idItemCarrito;
    private ProductoResumenDTOResponse producto;
    private Integer cantidad;
    private Long precioUnitario;
    private Long subtotal;
}
