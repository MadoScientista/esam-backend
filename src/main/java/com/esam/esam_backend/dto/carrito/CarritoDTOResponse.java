package com.esam.esam_backend.dto.carrito;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CarritoDTOResponse {

    private Long idCarrito;
    private List<ItemCarritoDTOResponse> items;
    private Integer cantidadTotal;
    private Long total;
}
