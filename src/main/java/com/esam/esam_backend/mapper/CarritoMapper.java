package com.esam.esam_backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.carrito.CarritoDTOResponse;
import com.esam.esam_backend.dto.carrito.ItemCarritoDTOResponse;
import com.esam.esam_backend.model.Carrito;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CarritoMapper {

    private final ItemCarritoMapper itemCarritoMapper;

    public CarritoDTOResponse toDTO(Carrito carrito) {
        List<ItemCarritoDTOResponse> items = carrito.getItems().stream()
                .map(itemCarritoMapper::toDTO)
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
}
