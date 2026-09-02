package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository pRepo;

    // Obtener según su id
    public Producto obtenerPorId(Long sku) {
        return pRepo.findById(sku)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con sku: " + sku));
    }

    // Obtener según su marca
    public List<Producto> obtenerPorMarca(Long idMarca) {
        return pRepo.findByMarcaIdMarca(idMarca);
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
        return pRepo.findByNombre(nombre);
    }

    // Guardar
    public Producto guardar(Producto producto) {
        return pRepo.save(producto);
    }

    // Editar
    public Producto editar(Long sku, Producto datos) {
        Producto producto = obtenerPorId(sku);
        producto.setNombre(datos.getNombre());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        producto.setMarca(datos.getMarca());
        return pRepo.save(producto);
    }

    // Borrar
    public void borrar(Long sku) {
        Producto producto = obtenerPorId(sku);
        pRepo.delete(producto);
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
        validarStock(unidades);
        Producto producto = obtenerPorId(sku);
        Long nuevoStock = producto.getStock() - unidades;
        if (nuevoStock < 0) {
            throw new RuntimeException("No hay stock suficiente. Stock actual: " + producto.getStock());
        }
        producto.setStock(nuevoStock);
        return pRepo.save(producto);
    }

    // Aumentar stock en las unidades especificadas
    public Producto aumentarStock(Long sku, Long unidades) {
        validarStock(unidades);
        Producto producto = obtenerPorId(sku);
        producto.setStock(producto.getStock() + unidades);
        return pRepo.save(producto);
    }

    private void validarStock(Long valor) {
        if (valor == null || valor < 0) {
            throw new RuntimeException("El stock debe ser un número entero positivo o cero");
        }
    }
}