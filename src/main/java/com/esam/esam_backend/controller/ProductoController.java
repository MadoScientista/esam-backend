package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esam.esam_backend.dto.producto.ProductoDTOResponse;
import com.esam.esam_backend.mapper.ProductoMapper;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    private final ProductoMapper productoMapper = new ProductoMapper();

    // Obtener un producto por su sku
    @GetMapping("/{sku}")
    public ProductoDTOResponse obtenerPorId(@PathVariable Long sku) {
        return productoMapper.toDTO(productoService.obtenerPorId(sku));
    }

    // Obtener productos según su marca
    @GetMapping("/marca/{idMarca}")
    public List<ProductoDTOResponse> obtenerPorMarca(@PathVariable Long idMarca) {
        return productoMapper.toDTOList(productoService.obtenerPorMarca(idMarca));
    }

    // Obtener productos según rango de precio
    @GetMapping("/precio")
    public List<ProductoDTOResponse> obtenerPorRangoPrecio(@RequestParam Long min, @RequestParam Long max) {
        return productoMapper.toDTOList(productoService.obtenerPorRangoPrecio(min, max));
    }

    // Obtener productos según rango de stock
    @GetMapping("/stock")
    public List<ProductoDTOResponse> obtenerPorRangoStock(@RequestParam Long min, @RequestParam Long max) {
        return productoMapper.toDTOList(productoService.obtenerPorRangoStock(min, max));
    }

    // Obtener productos según su nombre
    @GetMapping("/nombre/{nombre}")
    public List<ProductoDTOResponse> obtenerPorNombre(@PathVariable String nombre) {
        return productoMapper.toDTOList(productoService.obtenerPorNombre(nombre));
    }

    // Guardar un producto
    @PostMapping
    public ProductoDTOResponse guardar(@RequestBody Producto producto) {
        return productoMapper.toDTO(productoService.guardar(producto));
    }

    // Editar un producto
    @PutMapping("/{sku}")
    public ProductoDTOResponse editar(@PathVariable Long sku, @RequestBody Producto producto) {
        return productoMapper.toDTO(productoService.editar(sku, producto));
    }

    // Setear stock a un valor específico
    @PutMapping("/{sku}/stock/setear")
    public ProductoDTOResponse setearStock(@PathVariable Long sku, @RequestParam Long stock) {
        return productoMapper.toDTO(productoService.setearStock(sku, stock));
    }

    // Disminuir stock en las unidades especificadas
    @PutMapping("/{sku}/stock/disminuir")
    public ProductoDTOResponse disminuirStock(@PathVariable Long sku, @RequestParam Long unidades) {
        return productoMapper.toDTO(productoService.disminuirStock(sku, unidades));
    }

    // Aumentar stock en las unidades especificadas
    @PutMapping("/{sku}/stock/aumentar")
    public ProductoDTOResponse aumentarStock(@PathVariable Long sku, @RequestParam Long unidades) {
        return productoMapper.toDTO(productoService.aumentarStock(sku, unidades));
    }

    // Borrar un producto
    @DeleteMapping("/{sku}")
    public void borrar(@PathVariable Long sku) {
        productoService.borrar(sku);
    }
}
