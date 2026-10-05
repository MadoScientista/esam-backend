package com.esam.esam_backend.dto.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DashboardResumenDTOResponse {

    private long pedidosEntregados;
    private long pedidosPendientes;
    private long productosTotales;
    private long productosConStock;
    private long clientes;
    private long vendedores;
    private long administradores;
}
