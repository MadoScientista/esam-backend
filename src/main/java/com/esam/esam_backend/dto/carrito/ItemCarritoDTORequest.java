package com.esam.esam_backend.dto.carrito;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemCarritoDTORequest {

    @NotNull
    private Long idProducto;

    @NotNull
    @Min(1)
    @Max(99)
    private Integer cantidad;
}
