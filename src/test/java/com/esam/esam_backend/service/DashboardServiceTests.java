package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.esam.esam_backend.dto.dashboard.DashboardResumenDTOResponse;
import com.esam.esam_backend.enums.EstadoPedido;
import com.esam.esam_backend.repository.PedidoRepository;
import com.esam.esam_backend.repository.ProductoRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void obtieneLosConteosAgregadosDelDashboard() {
        when(pedidoRepository.countByEstado(EstadoPedido.ENTREGADO)).thenReturn(18L);
        when(pedidoRepository.countByEstado(EstadoPedido.PENDIENTE)).thenReturn(4L);
        when(productoRepository.count()).thenReturn(45L);
        when(productoRepository.countByStockGreaterThan(0)).thenReturn(38L);
        when(usuarioRepository.countByRolUsuario_Nombre("cliente")).thenReturn(210L);
        when(usuarioRepository.countByRolUsuario_Nombre("vendedor")).thenReturn(8L);
        when(usuarioRepository.countByRolUsuario_Nombre("admin")).thenReturn(2L);

        DashboardResumenDTOResponse resumen = dashboardService.obtenerResumen();

        assertEquals(18L, resumen.getPedidosEntregados());
        assertEquals(4L, resumen.getPedidosPendientes());
        assertEquals(45L, resumen.getProductosTotales());
        assertEquals(38L, resumen.getProductosConStock());
        assertEquals(210L, resumen.getClientes());
        assertEquals(8L, resumen.getVendedores());
        assertEquals(2L, resumen.getAdministradores());
    }
}
