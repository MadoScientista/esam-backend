package com.esam.esam_backend.dto.pedido;

import com.esam.esam_backend.enums.TipoEntrega;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PedidoDTORequest {
    @NotNull
    private TipoEntrega tipoEntrega;

    private Long idDireccion;
}
