package com.esam.esam_backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.carrito.CarritoDTOResponse;
import com.esam.esam_backend.dto.carrito.ItemCarritoDTOResponse;
import com.esam.esam_backend.dto.imagenProducto.ImagenProductoResponse;
import com.esam.esam_backend.dto.marca.MarcaDTO;
import com.esam.esam_backend.dto.producto.ProductoResumenDTOResponse;
import com.esam.esam_backend.model.Carrito;
import com.esam.esam_backend.model.ImagenProducto;
import com.esam.esam_backend.model.ItemCarrito;
import com.esam.esam_backend.model.Producto;

@Component
public class CarritoMapper {

    public CarritoDTOResponse toDTO(Carrito carrito) {
        List<ItemCarritoDTOResponse> items = carrito.getItems().stream()
                .map(this::toItemDTO)
                .toList();
        int cantidadTotal = 0;
        long total = 0;
        for (ItemCarritoDTOResponse item : items) {
            cantidadTotal = Math.addExact(cantidadTotal, item.getCantidad());
            total = Math.addExact(total, item.getSubtotal());
        }

        CarritoDTOResponse dto = new CarritoDTOResponse();
        dto.setIdCarrito(carrito.getIdCarrito());
        dto.setItems(items);
        dto.setCantidadTotal(cantidadTotal);
        dto.setTotal(total);
        return dto;
    }

    private ItemCarritoDTOResponse toItemDTO(ItemCarrito item) {
        Producto producto = item.getProducto();
        long subtotal = Math.multiplyExact(producto.getPrecio(), item.getCantidad().longValue());

        ItemCarritoDTOResponse dto = new ItemCarritoDTOResponse();
        dto.setIdItemCarrito(item.getIdItemCarrito());
        dto.setProducto(toProductoResumenDTO(producto));
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(producto.getPrecio());
        dto.setSubtotal(subtotal);
        return dto;
    }

    private ProductoResumenDTOResponse toProductoResumenDTO(Producto producto) {
        MarcaDTO marca = new MarcaDTO();
        marca.setIdMarca(producto.getMarca().getIdMarca());
        marca.setNombre(producto.getMarca().getNombre());

        ProductoResumenDTOResponse dto = new ProductoResumenDTOResponse();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setImagenPrincipal(imagenPrincipal(producto));
        dto.setMarca(marca);
        dto.setStock(producto.getStock());
        return dto;
    }

    private ImagenProductoResponse imagenPrincipal(Producto producto) {
        ImagenProducto principal = null;
        for (ImagenProducto imagen : producto.getImagenes()) {
            if (Boolean.TRUE.equals(imagen.getPrincipal())
                    && (principal == null
                            || Integer.compare(imagen.getOrden(), principal.getOrden()) < 0
                            || (imagen.getOrden().equals(principal.getOrden())
                                    && imagen.getIdImagenProducto() < principal.getIdImagenProducto()))) {
                principal = imagen;
            }
        }
        return principal == null ? null : toImagenDTO(principal);
    }

    private ImagenProductoResponse toImagenDTO(ImagenProducto imagen) {
        ImagenProductoResponse dto = new ImagenProductoResponse();
        dto.setIdImagenProducto(imagen.getIdImagenProducto());
        dto.setUrl(imagen.getUrl());
        dto.setTextoAlternativo(imagen.getTextoAlternativo());
        dto.setOrden(imagen.getOrden());
        dto.setPrincipal(imagen.getPrincipal());
        return dto;
    }
}
