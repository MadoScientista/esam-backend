package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.esam.esam_backend.model.Carrito;
import com.esam.esam_backend.model.ItemCarrito;
import com.esam.esam_backend.model.Producto;

class ItemCarritoServiceTests {

    private final ItemCarritoService service = new ItemCarritoService();

    @Test
    void agregaProductoYActualizaCantidadDeProductoExistente() {
        Carrito carrito = new Carrito();
        Producto producto = producto(12L);

        service.agregar(carrito, producto, 2);
        service.agregar(carrito, producto, 3);

        assertEquals(1, carrito.getItems().size());
        assertEquals(5, carrito.getItems().get(0).getCantidad());
        assertEquals(carrito, carrito.getItems().get(0).getCarrito());
        assertEquals(producto, carrito.getItems().get(0).getProducto());
    }

    @Test
    void rechazaCantidadCombinadaSuperiorAlMaximo() {
        Carrito carrito = carritoConItem(12L, 98);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.agregar(carrito, producto(12L), 2));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(98, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void cambiaCantidadDeProductoExistente() {
        Carrito carrito = carritoConItem(12L, 2);

        service.cambiarCantidad(carrito, 12L, 4);

        assertEquals(4, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void quitarEliminaSoloElProductoSolicitado() {
        Carrito carrito = carritoConItem(12L, 2);
        carrito.getItems().add(item(carrito, producto(13L), 1));

        service.quitar(carrito, 12L);

        assertEquals(1, carrito.getItems().size());
        assertEquals(13L, carrito.getItems().get(0).getProducto().getIdProducto());
    }

    @Test
    void cambiarCantidadDeProductoAusenteDevuelveNoEncontrado() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.cambiarCantidad(new Carrito(), 12L, 2));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void vaciarEliminaTodosLosItems() {
        Carrito carrito = carritoConItem(12L, 2);
        carrito.getItems().add(item(carrito, producto(13L), 1));

        service.vaciar(carrito);

        assertTrue(carrito.getItems().isEmpty());
    }

    private Carrito carritoConItem(Long idProducto, Integer cantidad) {
        Carrito carrito = new Carrito();
        carrito.setItems(new ArrayList<>(List.of(item(carrito, producto(idProducto), cantidad))));
        return carrito;
    }

    private ItemCarrito item(Carrito carrito, Producto producto, Integer cantidad) {
        ItemCarrito item = new ItemCarrito();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(cantidad);
        return item;
    }

    private Producto producto(Long idProducto) {
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        return producto;
    }
}
