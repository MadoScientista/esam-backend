package com.esam.esam_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.esam.esam_backend.model.Carrito;
import com.esam.esam_backend.model.ItemCarrito;
import com.esam.esam_backend.model.Producto;

@Service
public class ItemCarritoService {

    public void agregar(Carrito carrito, Producto producto, Integer cantidad) {
        ItemCarrito itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProducto().getIdProducto().equals(producto.getIdProducto()))
                .findFirst()
                .orElse(null);

        if (itemExistente == null) {
            ItemCarrito item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(cantidad);
            carrito.getItems().add(item);
            return;
        }

        int cantidadActualizada = itemExistente.getCantidad() + cantidad;
        if (cantidadActualizada > 99) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad de un producto en el carrito no puede superar 99");
        }
        itemExistente.setCantidad(cantidadActualizada);
    }

    public void cambiarCantidad(Carrito carrito, Long idProducto, Integer cantidad) {
        obtenerItem(carrito, idProducto).setCantidad(cantidad);
    }

    public void quitar(Carrito carrito, Long idProducto) {
        carrito.getItems().remove(obtenerItem(carrito, idProducto));
    }

    public void vaciar(Carrito carrito) {
        carrito.getItems().clear();
    }

    private ItemCarrito obtenerItem(Carrito carrito, Long idProducto) {
        return carrito.getItems().stream()
                .filter(item -> item.getProducto().getIdProducto().equals(idProducto))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El producto " + idProducto + " no está en el carrito"));
    }
}
