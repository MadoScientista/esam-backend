package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.producto.ProductoDTORequest;
import com.esam.esam_backend.dto.producto.ProductoDTOResponse;
import com.esam.esam_backend.exception.ProductoInvalidoException;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.MarcaRepository;

@Component 
public class ProductoMapper {

    @Autowired
    private MarcaRepository marcaRepository;


    public ProductoDTOResponse toDTO(Producto producto) {
        ProductoDTOResponse dto = new ProductoDTOResponse();
        dto.setSku(producto.getSku());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setMarca(producto.getMarca().getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImg(producto.getImg());
        return dto;
    }

    public List<ProductoDTOResponse> toDTOList(List<Producto> productos) {
        return productos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

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
            .orElseThrow(() -> new ProductoInvalidoException("No existe una marca con ID " + idMarca));
    }

    public Marca resolverMarcaParaEdicion(Long idMarca, Producto producto) {
        if (idMarca == null) {
            return producto.getMarca();
        }
        return resolverMarca(idMarca);
    }
}
