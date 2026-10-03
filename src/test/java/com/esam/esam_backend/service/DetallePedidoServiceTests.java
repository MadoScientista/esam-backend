package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.esam.esam_backend.model.DetallePedido;
import com.esam.esam_backend.model.Pedido;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.DetallePedidoRepository;

@ExtendWith(MockitoExtension.class)
class DetallePedidoServiceTests {

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @InjectMocks
    private DetallePedidoService detallePedidoService;

    @Test
    void creaDetalleConSnapshotYLoAsociaAlPedido() {
        Pedido pedido = new Pedido();
        Producto producto = new Producto();
        producto.setNombre("Producto");
        producto.setSku("SKU-1");
        producto.setPrecio(2500L);

        DetallePedido detalle = detallePedidoService.crear(pedido, producto, 3);

        assertEquals(pedido, detalle.getPedido());
        assertEquals(producto, detalle.getProducto());
        assertEquals("Producto", detalle.getNombreProducto());
        assertEquals("SKU-1", detalle.getSkuProducto());
        assertEquals(2500L, detalle.getPrecioUnitario());
        assertEquals(3, detalle.getCantidad());
        assertEquals(7500L, detalle.getSubtotal());
        assertEquals(List.of(detalle), pedido.getDetalles());
    }

    @Test
    void obtieneDetallesDeUnPedidoDesdeSuRepositorio() {
        List<DetallePedido> detalles = List.of(new DetallePedido());
        when(detallePedidoRepository.findByPedido_IdPedidoOrderByIdDetallePedidoAsc(8L))
                .thenReturn(detalles);

        assertEquals(detalles, detallePedidoService.obtenerPorPedido(8L));
        verify(detallePedidoRepository).findByPedido_IdPedidoOrderByIdDetallePedidoAsc(8L);
    }

    @Test
    void obtieneDetallesDeVariosPedidosEnUnaConsultaYLosAgrupa() {
        Pedido primerPedido = new Pedido();
        primerPedido.setIdPedido(3L);
        Pedido segundoPedido = new Pedido();
        segundoPedido.setIdPedido(4L);
        DetallePedido detalleUno = detalle(primerPedido);
        DetallePedido detalleDos = detalle(segundoPedido);
        when(detallePedidoRepository.buscarPorPedidos(List.of(3L, 4L)))
                .thenReturn(List.of(detalleUno, detalleDos));

        Map<Long, List<DetallePedido>> resultado =
                detallePedidoService.obtenerPorPedidos(List.of(3L, 4L));

        assertEquals(List.of(detalleUno), resultado.get(3L));
        assertEquals(List.of(detalleDos), resultado.get(4L));
        verify(detallePedidoRepository).buscarPorPedidos(List.of(3L, 4L));
    }

    private DetallePedido detalle(Pedido pedido) {
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        return detalle;
    }
}
