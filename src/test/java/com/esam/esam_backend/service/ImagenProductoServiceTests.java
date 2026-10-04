package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudinary.Cloudinary;
import com.esam.esam_backend.exception.ImagenNoEncontradaException;
import com.esam.esam_backend.exception.ProductoNoEncontradoException;
import com.esam.esam_backend.mapper.ImagenProductoMapper;
import com.esam.esam_backend.model.ImagenProducto;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.ImagenProductoRepository;
import com.esam.esam_backend.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ImagenProductoServiceTests {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private ImagenProductoRepository imagenProductoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ImagenProductoMapper imagenProductoMapper;

    @InjectMocks
    private ImagenProductoService imagenProductoService;

    @Test
    void marcarPrincipalDesmarcaLaAnteriorYGuardaLaSeleccion() {
        ImagenProducto anterior = imagen(10L, true);
        ImagenProducto seleccionada = imagen(11L, false);
        Producto producto = new Producto();
        producto.getImagenes().addAll(List.of(anterior, seleccionada));
        List<ImagenProducto> imagenes = producto.getImagenes();
        when(productoRepository.buscarPorIdParaPedido(7L)).thenReturn(Optional.of(producto));

        ImagenProducto resultado = imagenProductoService.marcarPrincipal(7L, 11L);

        assertSame(seleccionada, resultado);
        assertFalse(anterior.getPrincipal());
        assertTrue(seleccionada.getPrincipal());
        verify(imagenProductoRepository).saveAll(imagenes);
    }

    @Test
    void marcarPrincipalRechazaUnaImagenDeOtroProducto() {
        Producto producto = new Producto();
        producto.getImagenes().add(imagen(12L, false));
        when(productoRepository.buscarPorIdParaPedido(7L)).thenReturn(Optional.of(producto));

        assertThrows(ImagenNoEncontradaException.class,
                () -> imagenProductoService.marcarPrincipal(7L, 99L));

        verify(imagenProductoRepository, never()).saveAll(org.mockito.ArgumentMatchers.anyIterable());
    }

    @Test
    void marcarPrincipalRechazaProductoInexistente() {
        when(productoRepository.buscarPorIdParaPedido(7L)).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontradoException.class,
                () -> imagenProductoService.marcarPrincipal(7L, 11L));

        verify(imagenProductoRepository, never()).saveAll(org.mockito.ArgumentMatchers.anyIterable());
    }

    private ImagenProducto imagen(Long id, boolean principal) {
        ImagenProducto imagen = new ImagenProducto();
        imagen.setIdImagenProducto(id);
        imagen.setPrincipal(principal);
        return imagen;
    }
}