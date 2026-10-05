package com.esam.esam_backend.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.esam.esam_backend.dto.categoria.CategoriaDTO;
import com.esam.esam_backend.dto.producto.ProductoDTORequest;
import com.esam.esam_backend.model.Categoria;
import com.esam.esam_backend.model.Marca;
import com.esam.esam_backend.model.Producto;

@ExtendWith(MockitoExtension.class)
class ProductoMapperTests {

    @Mock
    private ImagenProductoMapper imagenProductoMapper;

    @Mock
    private MarcaMapper marcaMapper;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private ProductoMapper productoMapper;

    @Test
    void toEntityCopiaSkuMarcaYCategorias() {
        ProductoDTORequest request = new ProductoDTORequest();
        request.setSku("CUAD-001");
        request.setNombre("Cuaderno");
        request.setPrecio(2990L);
        request.setStock(10);
        Marca marca = new Marca();
        Categoria categoria = new Categoria();
        Set<Categoria> categorias = Set.of(categoria);

        Producto producto = productoMapper.toEntity(request, marca, categorias);

        assertEquals("CUAD-001", producto.getSku());
        assertEquals("Cuaderno", producto.getNombre());
        assertEquals(marca, producto.getMarca());
        assertEquals(categorias, producto.getCategorias());
    }

    @Test
    void toDTOIncluyeCategoriasDelProducto() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(5L);
        categoria.setNombre("Cuadernos");
        CategoriaDTO categoriaDTO = new CategoriaDTO();
        categoriaDTO.setIdCategoria(5L);
        categoriaDTO.setNombre("Cuadernos");
        Producto producto = new Producto();
        producto.setCategorias(Set.of(categoria));
        producto.setImagenes(List.of());
        producto.setMarca(new Marca());
        when(categoriaMapper.toDTO(categoria)).thenReturn(categoriaDTO);

        var response = productoMapper.toDTO(producto);

        assertEquals(List.of(categoriaDTO), response.getCategorias());
    }
}
