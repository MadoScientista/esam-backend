package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.esam.esam_backend.dto.productImage.ProductImageResponse;
import com.esam.esam_backend.mapper.ProductImageMapper;
import com.esam.esam_backend.model.ProductImage;
import com.esam.esam_backend.service.ProductImageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@RestController
public class ProductImageController {

    private final ProductImageService productImageService;

    private final ProductImageMapper piMapper;

    // Subir una imagen a un producto
    @PostMapping("/api/productos/{sku}/imagenes")
    public ResponseEntity<ProductImageResponse> guardar(
            @PathVariable Long sku,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "principal", defaultValue = "false") boolean principal) {

        ProductImage imagen = productImageService.guardar(sku, file, principal);

        return ResponseEntity.status(HttpStatus.CREATED).body(piMapper.toDTO(imagen));
    }

    // Todas las imágenes de un producto, la principal primero
    @GetMapping("/api/productos/{sku}/imagenes")
    public ResponseEntity<List<ProductImageResponse>> obtenerPorProducto(@PathVariable Long sku) {

        List<ProductImage> imagenes = productImageService.obtenerPorProducto(sku);

        if (imagenes == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(piMapper.toDTOList(imagenes));
    }

    // Marcar una imagen como principal
    @PutMapping("/api/productos/{sku}/imagenes/{idProductImage}/principal")
    public ResponseEntity<ProductImageResponse> editarPrincipal(
            @PathVariable Long sku,
            @PathVariable Long idProductImage) {

        ProductImage imagen = productImageService.editarPrincipal(sku, idProductImage);

        return ResponseEntity.ok(piMapper.toDTO(imagen));
    }

    // Borrar una imagen
    @DeleteMapping("/api/productos/{sku}/imagenes/{idProductImage}")
    public ResponseEntity<Void> borrar(
            @PathVariable Long sku,
            @PathVariable Long idProductImage) {

        productImageService.borrar(sku, idProductImage);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
