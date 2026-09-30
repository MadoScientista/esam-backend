package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esam.esam_backend.dto.producto.ProductoDTORequest;
import com.esam.esam_backend.dto.producto.ProductoDTOResponse;
import com.esam.esam_backend.mapper.ProductoMapper;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoMapper pMapper;

    // Obtener todos los productos
    @GetMapping()
    public ResponseEntity<List<ProductoDTOResponse>> obtenerProductos(){
        
        List<Producto> productos = productoService.obteneProductos();
        List<ProductoDTOResponse> dtoList = pMapper.toDTOList(productos);

        return ResponseEntity.ok(dtoList);
    }
    
    // Obtener un producto por su sku
    @GetMapping("/{sku}")
    public ResponseEntity<ProductoDTOResponse> obtenerPorId(@PathVariable Long sku) {
        
        Producto p = productoService.obtenerPorId(sku);
        ProductoDTOResponse dto = pMapper.toDTO(p);
        return ResponseEntity.ok(dto);
    }

    // Obtener productos filtrados por id de marca
    @GetMapping("/marca/{idONombreMarca}")
    public ResponseEntity<List<ProductoDTOResponse>> obtenerPorMarca(@PathVariable String idONombreMarca) {

        List<Producto> pLista = productoService.obtenerPorMarca(idONombreMarca);
        List<ProductoDTOResponse> dtoList = pMapper.toDTOList(pLista);

        return ResponseEntity.ok(dtoList);
    }


    // Obtener productos según rango de precio
    @GetMapping("/precio")
    public ResponseEntity<List<ProductoDTOResponse>> obtenerPorRangoPrecio(@RequestParam Long min, @RequestParam Long max) {

        List<Producto> pList = productoService.obtenerPorRangoPrecio(min, max);
        List<ProductoDTOResponse> dtoList = pMapper.toDTOList(pList);
        return ResponseEntity.ok(dtoList);
    }

    // Obtener productos según rango de stock
    @GetMapping("/stock")
    public ResponseEntity<List<ProductoDTOResponse>> obtenerPorRangoStock(@RequestParam Long min, @RequestParam Long max) {

        List<Producto> pList = productoService.obtenerPorRangoStock(min, max);
        List<ProductoDTOResponse> dtoList = pMapper.toDTOList(pList);
        return ResponseEntity.ok(dtoList);
    }

    // Filtra productos que contengan en su nombre lo indicado en el parámetro
    @GetMapping("/nombre")
    public ResponseEntity<List<ProductoDTOResponse>> obtenerPorNombre(@RequestParam  String nombre) {
        
        List<Producto> pList = productoService.obtenerPorNombre(nombre);
        List<ProductoDTOResponse> dtoList = pMapper.toDTOList(pList);
        return ResponseEntity.ok(dtoList);
    }

    // Guardar un producto
    @PostMapping
    public ResponseEntity<ProductoDTOResponse> guardar(@Valid @RequestBody ProductoDTORequest request) {
        
        Producto p = pMapper.toEntity(request);
        Producto pGuardado = productoService.guardar(p);
        ProductoDTOResponse dto = pMapper.toDTO(pGuardado);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // Editar un producto
    @PostMapping("/{sku}")
        public ResponseEntity<ProductoDTOResponse> editar(@PathVariable Long sku, @RequestBody @Valid ProductoDTORequest request) {
        
        Producto p = productoService.editar(sku, request);
        ProductoDTOResponse dto = pMapper.toDTO(p);
        return ResponseEntity.ok(dto);
    }

    // Setear stock a un valor específico
    @PutMapping("/{sku}/stock/setear")
    public ResponseEntity<ProductoDTOResponse> setearStock(@PathVariable Long sku, @RequestParam Long stock) {
        
        Producto p = productoService.setearStock(sku, stock);
        ProductoDTOResponse dto = pMapper.toDTO(p);
        return ResponseEntity.ok(dto);
    }

    // Disminuir stock en las unidades especificadas
    @PutMapping("/{sku}/stock/disminuir")
    public ProductoDTOResponse disminuirStock(@PathVariable Long sku, @RequestParam Long unidades) {
        return pMapper.toDTO(productoService.disminuirStock(sku, unidades));
    }

    // Aumentar stock en las unidades especificadas
    @PutMapping("/{sku}/stock/aumentar")
    public ProductoDTOResponse aumentarStock(@PathVariable Long sku, @RequestParam Long unidades) {
        return pMapper.toDTO(productoService.aumentarStock(sku, unidades));
    }

    // Borrar un producto
    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> borrar(@PathVariable Long sku) {
        
        productoService.borrar(sku);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Borrar un producto junto con todas sus imagenes
    @DeleteMapping("/{sku}/cascada")
    public ResponseEntity<Void> borrarEnCascada(@PathVariable Long sku) {

        productoService.borrarEnCascada(sku);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
