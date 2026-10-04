package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.esam.esam_backend.exception.ConflictoStockException;
import com.esam.esam_backend.exception.CategoriaNoEncontradaException;
import com.esam.esam_backend.exception.PedidoInvalidoException;
import com.esam.esam_backend.exception.ProductoNoEncontradoException;
import com.esam.esam_backend.mapper.ProductoMapper;
import com.esam.esam_backend.dto.producto.ProductoDTORequest;
import com.esam.esam_backend.model.Categoria;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.CategoriaRepository;
import com.esam.esam_backend.repository.MarcaRepository;
import com.esam.esam_backend.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTests {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MarcaRepository marcaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoMapper productoMapper;

    @Mock
    private ImagenProductoService imagenProductoService;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void obtenerProductoInexistenteLanzaExcepcionNotFound() {
        when(productoRepository.findById(42L)).thenReturn(Optional.empty());

        ProductoNoEncontradoException exception = assertThrows(
                ProductoNoEncontradoException.class,
                () -> productoService.obtenerPorId(42L));

        assertEquals("No existe un producto con id 42", exception.getMessage());
    }

    @Test
    void guardarResuelveMarcaYCategoriasAntesDePersistir() {
        ProductoDTORequest request = requestValido();
        Marca marca = new Marca();
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(5L);
        Producto producto = new Producto();

        when(marcaRepository.findById(2L)).thenReturn(Optional.of(marca));
        when(categoriaRepository.findAllById(Set.of(5L))).thenReturn(List.of(categoria));
        when(productoMapper.toEntity(request, marca, Set.of(categoria))).thenReturn(producto);
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.guardar(request);

        assertEquals(producto, resultado);
        verify(productoMapper).toEntity(request, marca, Set.of(categoria));
        verify(productoRepository).save(producto);
    }

    @Test
    void guardarRechazaCategoriaInexistenteAntesDeMapearOPersistir() {
        ProductoDTORequest request = requestValido();
        when(marcaRepository.findById(2L)).thenReturn(Optional.of(new Marca()));
        when(categoriaRepository.findAllById(Set.of(5L))).thenReturn(List.of());

        assertThrows(CategoriaNoEncontradaException.class,
                () -> productoService.guardar(request));

        verify(productoMapper, never()).toEntity(eq(request), any(Marca.class), any());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void reservarStockBloqueaYGuardaLaCantidadActualizada() {
        Producto producto = producto(7, true);
        when(productoRepository.buscarPorIdParaPedido(3L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = productoService.reservarStockParaPedido(3L, 2);

        assertEquals(producto, resultado);
        assertEquals(5, producto.getStock());
        verify(productoRepository).buscarPorIdParaPedido(3L);
        verify(productoRepository).save(producto);
    }

    @Test
    void reservarStockRechazaProductoInactivo() {
        when(productoRepository.buscarPorIdParaPedido(3L))
                .thenReturn(Optional.of(producto(7, false)));

        assertThrows(PedidoInvalidoException.class,
                () -> productoService.reservarStockParaPedido(3L, 2));

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void reservarStockRechazaCantidadSuperiorAlStock() {
        when(productoRepository.buscarPorIdParaPedido(3L))
                .thenReturn(Optional.of(producto(1, true)));

        assertThrows(ConflictoStockException.class,
                () -> productoService.reservarStockParaPedido(3L, 2));

        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void reponerStockUsaElProductoBloqueadoYGuardaElNuevoValor() {
        Producto producto = producto(5, true);
        when(productoRepository.buscarPorIdParaPedido(3L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);

        productoService.reponerStockPorCancelacion(3L, 2);

        assertEquals(7, producto.getStock());
        verify(productoRepository).buscarPorIdParaPedido(3L);
        verify(productoRepository).save(producto);
    }

    @Test
    void reponerStockRechazaDesbordamiento() {
        Producto producto = producto(Integer.MAX_VALUE, true);
        when(productoRepository.buscarPorIdParaPedido(3L)).thenReturn(Optional.of(producto));

        assertThrows(ConflictoStockException.class,
                () -> productoService.reponerStockPorCancelacion(3L, 1));

        verify(productoRepository, never()).save(any(Producto.class));
    }

    private Producto producto(Integer stock, boolean activo) {
        Producto producto = new Producto();
        producto.setIdProducto(3L);
        producto.setNombre("Producto");
        producto.setSku("SKU-3");
        producto.setPrecio(2500L);
        producto.setStock(stock);
        producto.setActivo(activo);
        return producto;
    }

    private ProductoDTORequest requestValido() {
        ProductoDTORequest request = new ProductoDTORequest();
        request.setSku("SKU-VALIDO");
        request.setNombre("Cuaderno");
        request.setPrecio(2500L);
        request.setStock(5);
        request.setIdMarca(2L);
        request.setIdCategorias(Set.of(5L));
        return request;
    }
}
