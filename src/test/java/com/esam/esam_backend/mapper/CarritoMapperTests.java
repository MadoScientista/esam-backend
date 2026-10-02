package com.esam.esam_backend.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esam.esam_backend.dto.carrito.CarritoDTOResponse;
import com.esam.esam_backend.dto.carrito.ItemCarritoDTOResponse;
import com.esam.esam_backend.model.Carrito;
import com.esam.esam_backend.model.ImagenProducto;
import com.esam.esam_backend.model.ItemCarrito;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.model.Producto;

class CarritoMapperTests {

    private final CarritoMapper mapper = new CarritoMapper();

    @Test
    void mapsItemsAndCalculatesCartTotals() {
        Marca marca = new Marca();
        marca.setIdMarca(4L);
        marca.setNombre("Marca");

        ImagenProducto imagenPrincipal = new ImagenProducto();
        imagenPrincipal.setIdImagenProducto(8L);
        imagenPrincipal.setUrl("https://example.com/producto.webp");
        imagenPrincipal.setTextoAlternativo("Producto");
        imagenPrincipal.setOrden(0);
        imagenPrincipal.setPrincipal(true);

        Producto producto = new Producto();
        producto.setIdProducto(12L);
        producto.setNombre("Producto");
        producto.setPrecio(2500L);
        producto.setStock(10);
        producto.setMarca(marca);
        producto.setImagenes(new ArrayList<>(List.of(imagenPrincipal)));

        ItemCarrito item = new ItemCarrito();
        item.setIdItemCarrito(15L);
        item.setCantidad(3);
        item.setProducto(producto);

        Carrito carrito = new Carrito();
        carrito.setIdCarrito(2L);
        carrito.setItems(new ArrayList<>(List.of(item)));

        CarritoDTOResponse response = mapper.toDTO(carrito);

        assertEquals(2L, response.getIdCarrito());
        assertEquals(3, response.getCantidadTotal());
        assertEquals(7500L, response.getTotal());
        assertEquals(1, response.getItems().size());

        ItemCarritoDTOResponse itemResponse = response.getItems().get(0);
        assertEquals(15L, itemResponse.getIdItemCarrito());
        assertEquals(2500L, itemResponse.getPrecioUnitario());
        assertEquals(7500L, itemResponse.getSubtotal());
        assertEquals(12L, itemResponse.getProducto().getIdProducto());
        assertEquals(4L, itemResponse.getProducto().getMarca().getIdMarca());
        assertNotNull(itemResponse.getProducto().getImagenPrincipal());
        assertEquals("Producto", itemResponse.getProducto().getImagenPrincipal().getTextoAlternativo());
    }
}
