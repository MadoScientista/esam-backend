package com.esam.esam_backend.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esam.esam_backend.model.DetallePedido;
import com.esam.esam_backend.model.Pedido;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.DetallePedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedido crear(Pedido pedido, Producto producto, Integer cantidad) {
        long subtotal = Math.multiplyExact(producto.getPrecio(), cantidad.longValue());
        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setNombreProducto(producto.getNombre());
        detalle.setSkuProducto(producto.getSku());
        detalle.setPrecioUnitario(producto.getPrecio());
        detalle.setCantidad(cantidad);
        detalle.setSubtotal(subtotal);
        pedido.getDetalles().add(detalle);
        return detalle;
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> obtenerPorPedido(Long idPedido) {
        return detallePedidoRepository.findByPedido_IdPedidoOrderByIdDetallePedidoAsc(idPedido);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<DetallePedido>> obtenerPorPedidos(List<Long> idsPedido) {
        if (idsPedido.isEmpty()) {
            return Map.of();
        }

        return detallePedidoRepository.buscarPorPedidos(idsPedido).stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getPedido().getIdPedido(),
                        LinkedHashMap::new,
                        Collectors.toList()));
    }
}
