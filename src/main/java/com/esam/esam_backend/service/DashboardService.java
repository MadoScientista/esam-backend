package com.esam.esam_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esam.esam_backend.dto.dashboard.DashboardResumenDTOResponse;
import com.esam.esam_backend.enums.EstadoPedido;
import com.esam.esam_backend.repository.PedidoRepository;
import com.esam.esam_backend.repository.ProductoRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public DashboardResumenDTOResponse obtenerResumen() {
        DashboardResumenDTOResponse resumen = new DashboardResumenDTOResponse();
        resumen.setPedidosEntregados(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO));
        resumen.setPedidosPendientes(pedidoRepository.countByEstado(EstadoPedido.PENDIENTE));
        resumen.setProductosTotales(productoRepository.count());
        resumen.setProductosConStock(productoRepository.countByStockGreaterThan(0));
        resumen.setClientes(usuarioRepository.countByRolUsuario_Nombre("cliente"));
        resumen.setVendedores(usuarioRepository.countByRolUsuario_Nombre("vendedor"));
        resumen.setAdministradores(usuarioRepository.countByRolUsuario_Nombre("admin"));
        return resumen;
    }
}
