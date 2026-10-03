package com.esam.esam_backend.mapper;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.carrito.ItemCarritoDTOResponse;
import com.esam.esam_backend.model.ItemCarrito;
import com.esam.esam_backend.model.Producto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ItemCarritoMapper {

    private final ProductoMapper productoMapper;

    public ItemCarritoDTOResponse toDTO(ItemCarrito item) {
        Producto producto = item.getProducto();
        long subtotal = Math.multiplyExact(producto.getPrecio(), item.getCantidad().longValue());

        ItemCarritoDTOResponse dto = new ItemCarritoDTOResponse();
        dto.setIdItemCarrito(item.getIdItemCarrito());
        dto.setProducto(productoMapper.toResumenDTO(producto));
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(producto.getPrecio());
        dto.setSubtotal(subtotal);
        return dto;
    }
}
