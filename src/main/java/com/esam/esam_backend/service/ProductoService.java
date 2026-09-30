package com.esam.esam_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.producto.ProductoDTORequest;
import com.esam.esam_backend.exception.ConflictoStockException;
import com.esam.esam_backend.exception.ProductoConImagenesException;
import com.esam.esam_backend.exception.ProductoInvalidoException;
import com.esam.esam_backend.exception.ProductoNoEncontradoException;
import com.esam.esam_backend.mapper.ProductoMapper;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository pRepo;

    @Autowired
    private ProductoMapper pMapper;

    @Autowired
    private ProductImageService productImageService;


    // Obtener todos los productos
    public List<Producto> obteneProductos(){
        return pRepo.findAllConImagenes();
    }

    // Obtener según su id
    public Producto obtenerPorId(Long sku) {
        return pRepo.findByIdConImagenes(sku)
                .orElseThrow(() -> new ProductoNoEncontradoException("No existe un producto con SKU " + sku));
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
    public Producto guardar(Producto producto) {
        validarPrecio(producto.getPrecio());
        validarStock(producto.getStock());
        return pRepo.save(producto);
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
        Marca marca = pMapper.resolverMarcaParaEdicion(datos.getIdMarca(), producto);
        producto.setMarca(marca);
        return pRepo.save(producto);
    }

    // Borrar
    public Producto borrar(Long sku) {
        Producto producto = obtenerPorId(sku);

        long imagenesAsociadas = productImageService.contarPorProducto(sku);

        if (imagenesAsociadas > 0) {
            throw new ProductoConImagenesException(
                    "El producto " + sku + " tiene " + imagenesAsociadas + " imagen(es) asociada(s)");
        }

        pRepo.delete(producto);
        return producto;
    }

    // Borrar el producto junto con todas sus imagenes
    public Producto borrarEnCascada(Long sku) {
        Producto producto = obtenerPorId(sku);

        productImageService.borrarTodas(sku);
        pRepo.delete(producto);

        return producto;
    }

    // Setear stock a un valor específico
    public Producto setearStock(Long sku, Long stock) {
        validarStock(stock);
        Producto producto = obtenerPorId(sku);
        producto.setStock(stock);
        return pRepo.save(producto);
    }

    // Disminuir stock en las unidades especificadas
    public Producto disminuirStock(Long sku, Long unidades) {
        validarUnidades(unidades);
        Producto producto = obtenerPorId(sku);
        if (unidades > producto.getStock()) {
            throw new ConflictoStockException("No hay stock suficiente. Stock actual: " + producto.getStock());
        }
        Long nuevoStock = producto.getStock() - unidades;
        producto.setStock(nuevoStock);
        return pRepo.save(producto);
    }

    // Aumentar stock en las unidades especificadas
    public Producto aumentarStock(Long sku, Long unidades) {
        validarUnidades(unidades);
        Producto producto = obtenerPorId(sku);
        if (producto.getStock() > Long.MAX_VALUE - unidades) {
            throw new ConflictoStockException("El aumento excede el stock máximo permitido");
        }
        producto.setStock(producto.getStock() + unidades);
        return pRepo.save(producto);
    }

    private void validarStock(Long valor) {
        if (valor == null || valor < 0) {
            throw new ProductoInvalidoException("El stock debe ser un número entero positivo o cero");
        }
    }

    private void validarPrecio(Long valor) {
        if (valor == null || valor < 0) {
            throw new ProductoInvalidoException("El precio debe ser un número positivo o cero");
        }
    }

    private void validarUnidades(Long unidades) {
        if (unidades == null || unidades <= 0) {
            throw new ProductoInvalidoException("Las unidades deben ser mayores que cero");
        }
    }
}