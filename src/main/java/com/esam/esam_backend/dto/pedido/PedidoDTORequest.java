package com.esam.esam_backend.dto.pedido;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoDTORequest {

    @NotNull
    private Long idDireccion;
}
