package com.esam.esam_backend.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.producto.ProductoDTORequest;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.MarcaRepository;

@Component
public class ProductoDTORequestMapper {

    @Autowired
    private MarcaRepository marcaRepository;

    public Producto toEntity(ProductoDTORequest dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setImg(dto.getImg());
        producto.setMarca(resolverMarca(dto.getIdMarca()));
        return producto;
    }

    private Marca resolverMarca(Long idMarca) {
        if (idMarca == null) {
            return null;
        }
        return marcaRepository.findById(idMarca)
                .orElseThrow(() -> new RuntimeException("Marca no encontrada con id: " + idMarca));
    }

    public Marca resolverMarcaParaEdicion(Long idMarca, Producto producto) {
        if (idMarca == null) {
            return producto.getMarca();
        }
        return resolverMarca(idMarca);
    }
}
