package com.esam.esam_backend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esam.esam_backend.dto.pedido.PedidoDTORequest;
import com.esam.esam_backend.dto.pedido.PedidoDTOResponse;
import com.esam.esam_backend.dto.pedido.PedidoEstadoDTORequest;
import com.esam.esam_backend.dto.pedido.PedidoResumenDTOResponse;
import com.esam.esam_backend.enums.EstadoPedido;
import com.esam.esam_backend.exception.DireccionNoEncontradaException;
import com.esam.esam_backend.exception.PedidoInvalidoException;
import com.esam.esam_backend.exception.PedidoNoEncontradoException;
import com.esam.esam_backend.exception.UsuarioNoEncontradaException;
import com.esam.esam_backend.mapper.PedidoMapper;
import com.esam.esam_backend.model.CambioEstadoPedido;
import com.esam.esam_backend.model.Carrito;
import com.esam.esam_backend.model.DetallePedido;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.model.ItemCarrito;
import com.esam.esam_backend.model.Pedido;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.CarritoRepository;
import com.esam.esam_backend.repository.DireccionRepository;
import com.esam.esam_backend.repository.PedidoRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoMapper pedidoMapper;
    private final DetallePedidoService detallePedidoService;
    private final ProductoService productoService;

    @Transactional
    public PedidoDTOResponse crear(Long idUsuario, PedidoDTORequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradaException(
                        "No existe un usuario con id " + idUsuario));
        Direccion direccion = direccionRepository
                .findByIdDireccionAndUsuarioIdUsuarioAndActivoTrue(request.getIdDireccion(), idUsuario)
                .orElseThrow(() -> new DireccionNoEncontradaException(
                        "No existe una dirección activa con id " + request.getIdDireccion()));
        Carrito carrito = carritoRepository.buscarParaPedidoPorUsuario(idUsuario)
                .orElseThrow(() -> new PedidoInvalidoException("El usuario no tiene un carrito"));

        if (carrito.getItems().isEmpty()) {
            throw new PedidoInvalidoException("No se puede crear un pedido con el carrito vacío");
        }

        List<ItemCarrito> items = carrito.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getProducto().getIdProducto()))
                .toList();
        Instant ahora = Instant.now();
        Pedido pedido = crearPedido(usuario, direccion, ahora);
        long total = 0;

        for (ItemCarrito item : items) {
            Producto producto = productoService.reservarStockParaPedido(
                    item.getProducto().getIdProducto(), item.getCantidad());

            DetallePedido detalle = detallePedidoService.crear(pedido, producto, item.getCantidad());
            total = Math.addExact(total, detalle.getSubtotal());
        }

        pedido.setTotal(total);
        registrarCambioEstado(pedido, null, EstadoPedido.PENDIENTE, usuario, ahora);

        Pedido guardado = pedidoRepository.save(pedido);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
        return pedidoMapper.toDTO(
                guardado,
                detallePedidoService.obtenerPorPedido(guardado.getIdPedido()));
    }

    @Transactional(readOnly = true)
    public List<PedidoResumenDTOResponse> obtenerHistorial(Long idUsuario) {
        List<Pedido> pedidos =
                pedidoRepository.findByUsuario_IdUsuarioOrderByCreadoEnDescIdPedidoDesc(idUsuario);
        return pedidoMapper.toResumenDTOList(
                pedidos,
                detallePedidoService.obtenerPorPedidos(
                        pedidos.stream().map(pedido -> pedido.getIdPedido()).toList()));
    }

    @Transactional(readOnly = true)
    public PedidoDTOResponse obtenerPorId(Long idPedido, Long idUsuario) {
        Pedido pedido = pedidoRepository.findByIdPedidoAndUsuario_IdUsuario(idPedido, idUsuario)
                .orElseThrow(() -> new PedidoNoEncontradoException(
                        "No existe un pedido con id " + idPedido));
        return pedidoMapper.toDTO(pedido, detallePedidoService.obtenerPorPedido(idPedido));
    }

    @Transactional(readOnly = true)
    public List<PedidoDTOResponse> obtenerTodosParaAdmin() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByCreadoEnDescIdPedidoDesc();
        Map<Long, List<DetallePedido>> detallesPorPedido = detallePedidoService.obtenerPorPedidos(
                pedidos.stream().map(pedido -> pedido.getIdPedido()).toList());
        List<PedidoDTOResponse> respuestas = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            respuestas.add(pedidoMapper.toAdminDTO(
                    pedido,
                    detallesPorPedido.getOrDefault(pedido.getIdPedido(), List.of())));
        }
        return respuestas;
    }

    @Transactional(readOnly = true)
    public PedidoDTOResponse obtenerPorIdParaAdmin(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNoEncontradoException(
                        "No existe un pedido con id " + idPedido));
        return pedidoMapper.toAdminDTO(pedido, detallePedidoService.obtenerPorPedido(idPedido));
    }

    @Transactional
    public PedidoDTOResponse cambiarEstado(
            Long idPedido,
            Long idUsuarioAdministrador,
            PedidoEstadoDTORequest request) {
        Pedido pedido = pedidoRepository.buscarPorIdParaActualizar(idPedido)
                .orElseThrow(() -> new PedidoNoEncontradoException(
                        "No existe un pedido con id " + idPedido));
        Usuario administrador = usuarioRepository.findById(idUsuarioAdministrador)
                .orElseThrow(() -> new UsuarioNoEncontradaException(
                        "No existe un usuario con id " + idUsuarioAdministrador));

        validarTransicion(pedido.getEstado(), request.getEstado());
        if (request.getEstado() == EstadoPedido.CANCELADO) {
            reponerStock(idPedido);
        }

        Instant ahora = Instant.now();
        registrarCambioEstado(pedido, pedido.getEstado(), request.getEstado(), administrador, ahora);
        pedido.setEstado(request.getEstado());
        Pedido guardado = pedidoRepository.save(pedido);
        return pedidoMapper.toAdminDTO(
                guardado,
                detallePedidoService.obtenerPorPedido(idPedido));
    }

    private Pedido crearPedido(Usuario usuario, Direccion direccion, Instant creadoEn) {
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("PED-" + UUID.randomUUID());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setCreadoEn(creadoEn);
        pedido.setNombreReceptor(direccion.getNombreReceptor());
        pedido.setTelefonoReceptor(direccion.getTelefonoReceptor());
        pedido.setCalle(direccion.getCalle());
        pedido.setNumero(direccion.getNumero());
        pedido.setComplemento(direccion.getComplemento());
        pedido.setComunaNombre(direccion.getComuna().getNombre());
        pedido.setRegionNombre(direccion.getComuna().getRegion().getNombre());
        pedido.setUsuario(usuario);
        pedido.setTotal(0L);
        return pedido;
    }

    private void validarTransicion(EstadoPedido actual, EstadoPedido siguiente) {
        boolean valida = switch (actual) {
            case PENDIENTE -> siguiente == EstadoPedido.CONFIRMADO || siguiente == EstadoPedido.CANCELADO;
            case CONFIRMADO -> siguiente == EstadoPedido.ENVIADO;
            case ENVIADO -> siguiente == EstadoPedido.ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };

        if (!valida) {
            throw new PedidoInvalidoException(
                    "No se permite cambiar el estado de " + actual + " a " + siguiente);
        }
    }

    private void reponerStock(Long idPedido) {
        List<DetallePedido> detalles = detallePedidoService.obtenerPorPedido(idPedido).stream()
                .sorted(Comparator.comparing(detalle -> detalle.getProducto().getIdProducto()))
                .toList();
        for (DetallePedido detalle : detalles) {
            productoService.reponerStockPorCancelacion(
                    detalle.getProducto().getIdProducto(), detalle.getCantidad());
        }
    }

    private void registrarCambioEstado(
            Pedido pedido,
            EstadoPedido anterior,
            EstadoPedido nuevo,
            Usuario usuario,
            Instant cambiadoEn) {
        CambioEstadoPedido cambio = new CambioEstadoPedido();
        cambio.setPedido(pedido);
        cambio.setEstadoAnterior(anterior);
        cambio.setEstadoNuevo(nuevo);
        cambio.setCambiadoEn(cambiadoEn);
        cambio.setUsuario(usuario);
        pedido.getHistorialEstados().add(cambio);
    }
}
