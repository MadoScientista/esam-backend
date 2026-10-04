package com.esam.esam_backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esam.esam_backend.dto.producto.ProductoDTORequest;
import com.esam.esam_backend.exception.CategoriaNoEncontradaException;
import com.esam.esam_backend.exception.ConflictoStockException;
import com.esam.esam_backend.exception.ProductoConImagenesException;
import com.esam.esam_backend.exception.ProductoInvalidoException;
import com.esam.esam_backend.exception.ProductoNoEncontradoException;
import com.esam.esam_backend.exception.PedidoInvalidoException;
import com.esam.esam_backend.mapper.ProductoMapper;
import com.esam.esam_backend.model.Categoria;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.CategoriaRepository;
import com.esam.esam_backend.repository.MarcaRepository;
import com.esam.esam_backend.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductoService {

    private final ProductoRepository pRepo;
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper pMapper;

    private final ImagenProductoService imagenProductoService;


    // Obtener todos los productos
    public List<Producto> obtenerProductos(){
        return pRepo.findAllConImagenes();
    }

    // Obtener según su id
    public Producto obtenerPorId(Long sku) {
        return pRepo.findById(sku)
                .orElseThrow(() -> new ProductoNoEncontradoException("No existe un producto con id " + sku));
    }

    // Obtener según su marca
    public List<Producto> obtenerPorMarca(String idONombreMarca) {

        List<Producto> productos = new ArrayList<>();

        if(idONombreMarca.matches("^\\d+$")){
            Long idMarca = Long.parseLong(idONombreMarca);
            productos = pRepo.findByMarcaIdMarca(idMarca);
        }else{
            productos = pRepo.findByMarcaNombre(idONombreMarca);
        }

        return productos;
    }

    // Obtener según rango de precio
    public List<Producto> obtenerPorRangoPrecio(Long precioMin, Long precioMax) {
        return pRepo.findByPrecioBetween(precioMin, precioMax);
    }

    // Obtener según rango de stock
    public List<Producto> obtenerPorRangoStock(Long stockMin, Long stockMax) {
        return pRepo.findByStockBetween(stockMin, stockMax);
    }

    // Obtener según su nombre
    public List<Producto> obtenerPorNombre(String nombre) {
        return pRepo.findByNombreContaining(nombre);
    }

    // Guardar
    public Producto guardar(ProductoDTORequest datos) {
        validarPrecio(datos.getPrecio());
        validarStock(datos.getStock());
        Producto producto = pMapper.toEntity(
                datos,
                resolverMarca(datos.getIdMarca()),
                resolverCategorias(datos.getIdCategorias()));
        return pRepo.save(producto);
    }

    private Set<Categoria> resolverCategorias(Set<Long> idsCategorias) {
        List<Categoria> categorias = categoriaRepository.findAllById(idsCategorias);
        Set<Long> idsEncontrados = new HashSet<>();
        for (Categoria categoria : categorias) {
            idsEncontrados.add(categoria.getIdCategoria());
        }

        for (Long idCategoria : idsCategorias) {
            if (!idsEncontrados.contains(idCategoria)) {
                throw new CategoriaNoEncontradaException(
                        "No existe una categoría con id " + idCategoria);
            }
        }

        return new HashSet<>(categorias);
    }

    // Editar
    public Producto editar(Long sku, ProductoDTORequest datos) {
        validarPrecio(datos.getPrecio());
        validarStock(datos.getStock());
        Producto producto = obtenerPorId(sku);
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        if (datos.getIdMarca() != null) {
            producto.setMarca(resolverMarca(datos.getIdMarca()));
        }
        return pRepo.save(producto);
    }

    private Marca resolverMarca(Long idMarca) {
        if (idMarca == null) {
            return null;
        }
        return marcaRepository.findById(idMarca)
                .orElseThrow(() -> new ProductoInvalidoException(
                        "No existe una marca con ID " + idMarca));
    }

    // Borrar
    public Producto borrar(Long sku) {
        Producto producto = obtenerPorId(sku);

        long imagenesAsociadas = imagenProductoService.contarPorProducto(sku);

        if (imagenesAsociadas > 0) {
            throw new ProductoConImagenesException(
                    "El producto " + sku + " tiene " + imagenesAsociadas + " imagen(es) asociada(s)");
        }

        pRepo.delete(producto);
        return producto;
    }

    // Borrar el producto junto con todas sus imagenes
    @Transactional
    public Producto borrarEnCascada(Long sku) {
        Producto producto = obtenerPorId(sku);

        imagenProductoService.borrarTodas(sku);
        pRepo.delete(producto);

        return producto;
    }

    // Setear stock a un valor específico
    public Producto setearStock(Long sku, Integer stock) {
        validarStock(stock);
        Producto producto = obtenerPorId(sku);
        producto.setStock(stock);
        return pRepo.save(producto);
    }

    // Disminuir stock en las unidades especificadas
    public Producto disminuirStock(Long sku, Integer unidades) {
        validarUnidades(unidades);
        Producto producto = obtenerPorId(sku);
        if (unidades > producto.getStock()) {
            throw new ConflictoStockException("No hay stock suficiente. Stock actual: " + producto.getStock());
        }
        Integer nuevoStock = producto.getStock() - unidades;
        producto.setStock(nuevoStock);
        return pRepo.save(producto);
    }

    // Aumentar stock en las unidades especificadas
    public Producto aumentarStock(Long sku, Integer unidades) {
        validarUnidades(unidades);
        Producto producto = obtenerPorId(sku);
        if (producto.getStock() > Long.MAX_VALUE - unidades) {
            throw new ConflictoStockException("El aumento excede el stock máximo permitido");
        }
        producto.setStock(producto.getStock() + unidades);
        return pRepo.save(producto);
    }

    public Producto reservarStockParaPedido(Long idProducto, Integer cantidad) {
        Producto producto = pRepo.buscarPorIdParaPedido(idProducto)
                .orElseThrow(() -> new PedidoInvalidoException(
                        "Uno de los productos del carrito ya no está disponible"));
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new PedidoInvalidoException(
                    "El producto " + producto.getIdProducto() + " ya no está disponible");
        }
        if (producto.getStock() < cantidad) {
            throw new ConflictoStockException(
                    "Stock insuficiente para el producto " + producto.getIdProducto());
        }

        producto.setStock(producto.getStock() - cantidad);
        return pRepo.save(producto);
    }

    public void reponerStockPorCancelacion(Long idProducto, Integer cantidad) {
        Producto producto = pRepo.buscarPorIdParaPedido(idProducto)
                .orElseThrow(() -> new PedidoInvalidoException(
                        "No se puede reponer el stock de un producto inexistente"));
        try {
            producto.setStock(Math.addExact(producto.getStock(), cantidad));
        } catch (ArithmeticException exception) {
            throw new ConflictoStockException(
                    "No se puede reponer el stock del producto " + producto.getIdProducto());
        }
        pRepo.save(producto);
    }

    private void validarStock(Integer valor) {
        if (valor == null || valor < 0) {
            throw new ProductoInvalidoException("El stock debe ser un número entero positivo o cero");
        }
    }

    private void validarPrecio(Long valor) {
        if (valor == null || valor < 0) {
            throw new ProductoInvalidoException("El precio debe ser un número positivo o cero");
        }
    }

    private void validarUnidades(Integer unidades) {
        if (unidades == null || unidades <= 0) {
            throw new ProductoInvalidoException("Las unidades deben ser mayores que cero");
        }
    }
}