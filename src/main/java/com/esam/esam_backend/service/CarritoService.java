package com.esam.esam_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esam.esam_backend.dto.carrito.CarritoDTOResponse;
import com.esam.esam_backend.dto.carrito.ItemCarritoCantidadDTORequest;
import com.esam.esam_backend.dto.carrito.ItemCarritoDTORequest;
import com.esam.esam_backend.exception.ProductoNoEncontradoException;
import com.esam.esam_backend.exception.UsuarioNoEncontradaException;
import com.esam.esam_backend.mapper.CarritoMapper;
import com.esam.esam_backend.model.Carrito;
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
    private final ItemCarritoService itemCarritoService;

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

        itemCarritoService.agregar(carrito, producto, request.getCantidad());

        return carritoMapper.toDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public CarritoDTOResponse cambiarCantidad(
            Long idUsuario,
            Long idProducto,
            ItemCarritoCantidadDTORequest request) {
        Carrito carrito = obtenerCarrito(idUsuario);
        itemCarritoService.cambiarCantidad(carrito, idProducto, request.getCantidad());
        return carritoMapper.toDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public CarritoDTOResponse quitarItem(Long idUsuario, Long idProducto) {
        Carrito carrito = obtenerCarrito(idUsuario);
        itemCarritoService.quitar(carrito, idProducto);
        return carritoMapper.toDTO(carritoRepository.save(carrito));
    }

    @Transactional
    public void vaciar(Long idUsuario) {
        Carrito carrito = obtenerCarrito(idUsuario);
        itemCarritoService.vaciar(carrito);
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
}
