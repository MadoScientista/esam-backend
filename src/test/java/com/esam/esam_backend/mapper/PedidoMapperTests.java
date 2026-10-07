package com.esam.esam_backend.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esam.esam_backend.dto.pedido.PedidoAdminDTOResponse;
import com.esam.esam_backend.dto.pedido.PedidoDTOResponse;
import com.esam.esam_backend.enums.EstadoPedido;
import com.esam.esam_backend.enums.TipoEntrega;
import com.esam.esam_backend.model.CambioEstadoPedido;
import com.esam.esam_backend.model.DetallePedido;
import com.esam.esam_backend.model.Pedido;
import com.esam.esam_backend.model.Usuario;

class PedidoMapperTests {

    private final PedidoMapper mapper = new PedidoMapper();

    @Test
    void toDTODespachoIncluyeDatosDeEnvio() {
        Pedido pedido = pedido(TipoEntrega.DESPACHO);
        pedido.setNombreReceptor("Receptor");
        pedido.setCalle("Calle");

        PedidoDTOResponse response = mapper.toDTO(pedido, List.of());

        assertEquals("DESPACHO", response.getTipoEntrega());
        assertNotNull(response.getEnvio());
        assertEquals("Receptor", response.getEnvio().getNombreReceptor());
        assertEquals("Calle", response.getEnvio().getCalle());
    }

    @Test
    void toDTORetiroEnTiendaNoIncluyeDatosDeEnvio() {
        Pedido pedido = pedido(TipoEntrega.RETIRA_TIENDA);

        PedidoDTOResponse response = mapper.toDTO(pedido, List.of());

        assertEquals("RETIRA_TIENDA", response.getTipoEntrega());
        assertNull(response.getEnvio());
    }

    @Test
    void toAdminDTOConservaTipoEntregaYExponeAlUsuario() {
        Pedido pedido = pedido(TipoEntrega.DESPACHO);
        pedido.setNombreReceptor("Receptor");
        List<DetallePedido> detalles = List.of();

        PedidoAdminDTOResponse response = mapper.toAdminDTO(pedido, detalles);

        assertEquals("DESPACHO", response.getTipoEntrega());
        assertEquals(7L, response.getIdUsuario());
        assertEquals("cliente@ejemplo.com", response.getEmailUsuario());
        assertEquals(EstadoPedido.PENDIENTE, response.getEstado());
        assertEquals(detalles, response.getDetalles());
    }

    private static Pedido pedido(TipoEntrega tipoEntrega) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(7L);
        usuario.setCorreo("cliente@ejemplo.com");

        CambioEstadoPedido cambio = new CambioEstadoPedido();
        cambio.setIdCambioEstadoPedido(1L);
        cambio.setEstadoAnterior(null);
        cambio.setEstadoNuevo(EstadoPedido.PENDIENTE);
        cambio.setCambiadoEn(Instant.parse("2026-01-01T00:00:00Z"));

        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setNumeroPedido("PED-1");
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTipoEntrega(tipoEntrega);
        pedido.setTotal(5000L);
        pedido.setCreadoEn(Instant.parse("2026-01-01T00:00:00Z"));
        pedido.setUsuario(usuario);
        pedido.setHistorialEstados(new ArrayList<>(List.of(cambio)));
        return pedido;
    }
}
