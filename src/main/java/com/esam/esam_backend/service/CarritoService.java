package com.esam.esam_backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.esam.esam_backend.dto.carrito.CarritoDTOResponse;
import com.esam.esam_backend.dto.carrito.ItemCarritoCantidadDTORequest;
import com.esam.esam_backend.dto.carrito.ItemCarritoDTORequest;
import com.esam.esam_backend.exception.ProductoNoEncontradoException;
import com.esam.esam_backend.exception.UsuarioNoEncontradaException;
import com.esam.esam_backend.mapper.CarritoMapper;
import com.esam.esam_backend.model.Carrito;
import com.esam.esam_backend.model.ItemCarrito;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.CarritoRepository;
import com.esam.esam_backend.repository.ProductoRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CarritoMapper carritoMapper;

    @Transactional
    public CarritoDTOResponse obtenerOCrear(Long idUsuario) {
        return carritoMapper.toDTO(obtenerCarrito(idUsuario));
    }

    @Transactional
    public CarritoDTOResponse agregarItem(Long idUsuario, ItemCarritoDTORequest request) {
        Carrito carrito = obtenerCarrito(idUsuario);
        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + request.getIdProducto()));

        ItemCarrito itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProducto().getIdProducto().equals(producto.getIdProducto()))
                .findFirst()
                .orElse(null);

        if (itemExistente == null) {
            ItemCarrito item = new ItemCarrito();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(request.getCantidad());
            carrito.getItems().add(item);
        } else {
            int cantidadActualizada = itemExistente.getCantidad() + request.getCantidad();
            if (cantidadActualizada > 99) {
                throw new IllegalArgumentException("La cantidad de un producto en el carrito no puede superar 99");
            }
            itemExistente.setCantidad(cantidadActualizada);
        }

        return carritoMapper.toDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public CarritoDTOResponse cambiarCantidad(
            Long idUsuario,
            Long idProducto,
            ItemCarritoCantidadDTORequest request) {
        Carrito carrito = obtenerCarrito(idUsuario);
        ItemCarrito item = obtenerItem(carrito, idProducto);
        item.setCantidad(request.getCantidad());
        return carritoMapper.toDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public CarritoDTOResponse quitarItem(Long idUsuario, Long idProducto) {
        Carrito carrito = obtenerCarrito(idUsuario);
        carrito.getItems().remove(obtenerItem(carrito, idProducto));
        return carritoMapper.toDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public void vaciar(Long idUsuario) {
        Carrito carrito = obtenerCarrito(idUsuario);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    private Carrito obtenerCarrito(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradaException(
                        "No existe un usuario con id " + idUsuario));

        return carritoRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseGet(() -> {
                    Carrito carrito = new Carrito();
                    carrito.setUsuario(usuario);
                    return carritoRepository.save(carrito);
                });
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
