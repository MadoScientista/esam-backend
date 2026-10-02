package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.esam.esam_backend.dto.pedido.PedidoDTORequest;
import com.esam.esam_backend.dto.pedido.PedidoDTOResponse;
import com.esam.esam_backend.dto.pedido.PedidoEstadoDTORequest;
import com.esam.esam_backend.dto.pedido.PedidoAdminDTOResponse;
import com.esam.esam_backend.enums.EstadoPedido;
import com.esam.esam_backend.exception.ConflictoStockException;
import com.esam.esam_backend.exception.PedidoInvalidoException;
import com.esam.esam_backend.mapper.PedidoMapper;
import com.esam.esam_backend.model.Carrito;
import com.esam.esam_backend.model.CambioEstadoPedido;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.DetallePedido;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.model.ItemCarrito;
import com.esam.esam_backend.model.Pedido;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.model.Region;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.CarritoRepository;
import com.esam.esam_backend.repository.DireccionRepository;
import com.esam.esam_backend.repository.PedidoRepository;
import com.esam.esam_backend.repository.ProductoRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTests {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void crearPedidoCopiaDatosCalculaTotalYDescuentaStock() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(7L);
        Carrito carrito = new Carrito();
        Producto producto = producto(3L, 2500L, 10);
        carrito.setItems(new ArrayList<>(List.of(item(producto, 2))));
        Direccion direccion = direccion();
        PedidoDTORequest request = new PedidoDTORequest();
        request.setIdDireccion(9L);
        PedidoDTOResponse response = new PedidoDTOResponse();

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));
        when(direccionRepository.findByIdDireccionAndUsuarioIdUsuarioAndActivoTrue(9L, 7L))
                .thenReturn(Optional.of(direccion));
        when(carritoRepository.buscarParaPedidoPorUsuario(7L)).thenReturn(Optional.of(carrito));
        when(productoRepository.buscarPorIdParaPedido(3L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoMapper.toDTO(any(Pedido.class))).thenReturn(response);

        PedidoDTOResponse resultado = pedidoService.crear(7L, request);

        assertEquals(response, resultado);
        assertEquals(8, producto.getStock());
        assertEquals(0, carrito.getItems().size());

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(pedidoCaptor.capture());
        Pedido pedido = pedidoCaptor.getValue();
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(5000L, pedido.getTotal());
        assertNotNull(pedido.getCreadoEn());
        assertEquals("Receptor", pedido.getNombreReceptor());
        assertEquals("Comuna", pedido.getComunaNombre());
        assertEquals("Región", pedido.getRegionNombre());
        assertEquals(1, pedido.getDetalles().size());
        DetallePedido detalle = pedido.getDetalles().get(0);
        assertEquals("Producto 3", detalle.getNombreProducto());
        assertEquals("SKU-3", detalle.getSkuProducto());
        assertEquals(5000L, detalle.getSubtotal());
        assertEquals(1, pedido.getHistorialEstados().size());
        CambioEstadoPedido cambioInicial = pedido.getHistorialEstados().get(0);
        assertNull(cambioInicial.getEstadoAnterior());
        assertEquals(EstadoPedido.PENDIENTE, cambioInicial.getEstadoNuevo());
    }

    @Test
    void crearPedidoFallaSiNoHayStockSuficiente() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(7L);
        Carrito carrito = new Carrito();
        Producto producto = producto(3L, 2500L, 1);
        carrito.setItems(new ArrayList<>(List.of(item(producto, 2))));
        PedidoDTORequest request = new PedidoDTORequest();
        request.setIdDireccion(9L);

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuario));
        when(direccionRepository.findByIdDireccionAndUsuarioIdUsuarioAndActivoTrue(9L, 7L))
                .thenReturn(Optional.of(direccion()));
        when(carritoRepository.buscarParaPedidoPorUsuario(7L)).thenReturn(Optional.of(carrito));
        when(productoRepository.buscarPorIdParaPedido(3L)).thenReturn(Optional.of(producto));

        assertThrows(ConflictoStockException.class, () -> pedidoService.crear(7L, request));
        verifyNoInteractions(pedidoMapper);
    }

    @Test
    void cancelarPedidoPendienteReponeStockYRegistraCambio() {
        Usuario administrador = new Usuario();
        administrador.setIdUsuario(2L);
        Producto producto = producto(3L, 2500L, 4);
        Pedido pedido = new Pedido();
        pedido.setIdPedido(12L);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        pedido.setDetalles(new ArrayList<>(List.of(detalle)));
        pedido.setHistorialEstados(new ArrayList<>());

        PedidoEstadoDTORequest request = new PedidoEstadoDTORequest();
        request.setEstado(EstadoPedido.CANCELADO);
        PedidoAdminDTOResponse response = new PedidoAdminDTOResponse();

        when(pedidoRepository.buscarPorIdParaActualizar(12L)).thenReturn(Optional.of(pedido));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(administrador));
        when(productoRepository.buscarPorIdParaPedido(3L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoMapper.toAdminDTO(pedido)).thenReturn(response);

        PedidoDTOResponse resultado = pedidoService.cambiarEstado(12L, 2L, request);

        assertEquals(response, resultado);
        assertEquals(6, producto.getStock());
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
        assertEquals(1, pedido.getHistorialEstados().size());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getHistorialEstados().get(0).getEstadoAnterior());
        assertEquals(EstadoPedido.CANCELADO, pedido.getHistorialEstados().get(0).getEstadoNuevo());
        verify(productoRepository).save(producto);
    }

    @Test
    void rechazaTransicionDeEstadoNoPermitida() {
        Usuario administrador = new Usuario();
        administrador.setIdUsuario(2L);
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.PENDIENTE);
        PedidoEstadoDTORequest request = new PedidoEstadoDTORequest();
        request.setEstado(EstadoPedido.ENVIADO);

        when(pedidoRepository.buscarPorIdParaActualizar(12L)).thenReturn(Optional.of(pedido));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(administrador));

        assertThrows(PedidoInvalidoException.class, () -> pedidoService.cambiarEstado(12L, 2L, request));
    }

    private static ItemCarrito item(Producto producto, int cantidad) {
        ItemCarrito item = new ItemCarrito();
        item.setProducto(producto);
        item.setCantidad(cantidad);
        return item;
    }

    private static Producto producto(Long id, Long precio, int stock) {
        Producto producto = new Producto();
        producto.setIdProducto(id);
        producto.setSku("SKU-" + id);
        producto.setNombre("Producto " + id);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setActivo(true);
        return producto;
    }

    private static Direccion direccion() {
        Region region = new Region();
        region.setNombre("Región");
        Comuna comuna = new Comuna();
        comuna.setNombre("Comuna");
        comuna.setRegion(region);
        Direccion direccion = new Direccion();
        direccion.setNombreReceptor("Receptor");
        direccion.setTelefonoReceptor("123456789");
        direccion.setCalle("Calle");
        direccion.setNumero("1");
        direccion.setComuna(comuna);
        return direccion;
    }
}
