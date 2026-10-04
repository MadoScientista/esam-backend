package com.esam.esam_backend.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.producto.ProductoDTORequest;
import com.esam.esam_backend.dto.producto.ProductoDTOResponse;
import com.esam.esam_backend.dto.producto.ProductoResumenDTOResponse;
import com.esam.esam_backend.model.Categoria;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.model.Producto;

@Component 
public class ProductoMapper {

    private final ImagenProductoMapper imagenProductoMapper;
    private final MarcaMapper marcaMapper;

    public ProductoMapper(ImagenProductoMapper imagenProductoMapper, MarcaMapper marcaMapper) {
        this.imagenProductoMapper = imagenProductoMapper;
        this.marcaMapper = marcaMapper;
    }

    public ProductoDTOResponse toDTO(Producto producto) {
        ProductoDTOResponse dto = new ProductoDTOResponse();
        dto.setIdProducto(producto.getIdProducto());
        dto.setSku(producto.getSku());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setMarca(producto.getMarca().getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setMarcaDetalle(marcaMapper.toDTO(producto.getMarca()));
        dto.setImagenes(imagenProductoMapper.toDTOList(producto.getImagenes()));
        return dto;
    }

    public ProductoResumenDTOResponse toResumenDTO(Producto producto) {
        ProductoResumenDTOResponse dto = new ProductoResumenDTOResponse();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setImagenPrincipal(imagenProductoMapper.toImagenPrincipalDTO(producto));
        dto.setMarca(marcaMapper.toDTO(producto.getMarca()));
        dto.setStock(producto.getStock());
        return dto;
    }

    public List<ProductoDTOResponse> toDTOList(List<Producto> productos) {
        return productos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Producto toEntity(ProductoDTORequest dto, Marca marca, Set<Categoria> categorias) {
        Producto producto = new Producto();
        producto.setSku(dto.getSku());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setMarca(marca);
        producto.setCategorias(categorias);
        return producto;
    }
}
