package com.esam.esam_backend.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.pedido.CambioEstadoPedidoDTOResponse;
import com.esam.esam_backend.dto.pedido.DatosEnvioDTOResponse;
import com.esam.esam_backend.dto.pedido.DetallePedidoDTOResponse;
import com.esam.esam_backend.dto.pedido.PedidoAdminDTOResponse;
import com.esam.esam_backend.dto.pedido.PedidoDTOResponse;
import com.esam.esam_backend.dto.pedido.PedidoResumenDTOResponse;
import com.esam.esam_backend.model.CambioEstadoPedido;
import com.esam.esam_backend.model.DetallePedido;
import com.esam.esam_backend.model.Pedido;

@Component
public class PedidoMapper {

    public PedidoDTOResponse toDTO(Pedido pedido) {
        List<CambioEstadoPedido> historial = new ArrayList<>(pedido.getHistorialEstados());
        historial.sort((primero, segundo) -> {
            int comparacionFecha = primero.getCambiadoEn().compareTo(segundo.getCambiadoEn());
            if (comparacionFecha != 0) {
                return comparacionFecha;
            }
            return Long.compare(
                    primero.getIdCambioEstadoPedido(),
                    segundo.getIdCambioEstadoPedido());
        });
        List<CambioEstadoPedidoDTOResponse> cambiosEstado = new ArrayList<>();
        for (CambioEstadoPedido cambio : historial) {
            cambiosEstado.add(toCambioEstadoDTO(cambio));
        }

        PedidoDTOResponse dto = new PedidoDTOResponse();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setNumeroPedido(pedido.getNumeroPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setCreadoEn(pedido.getCreadoEn());
        dto.setEnvio(toDatosEnvioDTO(pedido));
        dto.setDetalles(pedido.getDetalles().stream()
                .map(this::toDetalleDTO)
                .toList());
        dto.setHistorialEstados(cambiosEstado);
        return dto;
    }

    public PedidoAdminDTOResponse toAdminDTO(Pedido pedido) {
        PedidoDTOResponse base = toDTO(pedido);
        PedidoAdminDTOResponse dto = new PedidoAdminDTOResponse();
        dto.setIdPedido(base.getIdPedido());
        dto.setNumeroPedido(base.getNumeroPedido());
        dto.setEstado(base.getEstado());
        dto.setTotal(base.getTotal());
        dto.setCreadoEn(base.getCreadoEn());
        dto.setEnvio(base.getEnvio());
        dto.setDetalles(base.getDetalles());
        dto.setHistorialEstados(base.getHistorialEstados());
        dto.setIdUsuario(pedido.getUsuario().getIdUsuario());
        dto.setEmailUsuario(pedido.getUsuario().getCorreo());
        return dto;
    }

    public List<PedidoResumenDTOResponse> toResumenDTOList(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(this::toResumenDTO)
                .toList();
    }

    public PedidoResumenDTOResponse toResumenDTO(Pedido pedido) {
        int cantidadItems = 0;
        for (DetallePedido detalle : pedido.getDetalles()) {
            cantidadItems = Math.addExact(cantidadItems, detalle.getCantidad());
        }

        PedidoResumenDTOResponse dto = new PedidoResumenDTOResponse();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setNumeroPedido(pedido.getNumeroPedido());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setCantidadItems(cantidadItems);
        dto.setCreadoEn(pedido.getCreadoEn());
        return dto;
    }

    private DatosEnvioDTOResponse toDatosEnvioDTO(Pedido pedido) {
        DatosEnvioDTOResponse dto = new DatosEnvioDTOResponse();
        dto.setNombreReceptor(pedido.getNombreReceptor());
        dto.setTelefonoReceptor(pedido.getTelefonoReceptor());
        dto.setCalle(pedido.getCalle());
        dto.setNumero(pedido.getNumero());
        dto.setComplemento(pedido.getComplemento());
        dto.setComunaNombre(pedido.getComunaNombre());
        dto.setRegionNombre(pedido.getRegionNombre());
        return dto;
    }

    private DetallePedidoDTOResponse toDetalleDTO(DetallePedido detalle) {
        DetallePedidoDTOResponse dto = new DetallePedidoDTOResponse();
        dto.setIdDetallePedido(detalle.getIdDetallePedido());
        dto.setIdProducto(detalle.getProducto().getIdProducto());
        dto.setNombreProducto(detalle.getNombreProducto());
        dto.setSkuProducto(detalle.getSkuProducto());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setCantidad(detalle.getCantidad());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }

    private CambioEstadoPedidoDTOResponse toCambioEstadoDTO(CambioEstadoPedido cambio) {
        CambioEstadoPedidoDTOResponse dto = new CambioEstadoPedidoDTOResponse();
        dto.setEstadoAnterior(cambio.getEstadoAnterior());
        dto.setEstadoNuevo(cambio.getEstadoNuevo());
        dto.setCambiadoEn(cambio.getCambiadoEn());
        return dto;
    }
}
