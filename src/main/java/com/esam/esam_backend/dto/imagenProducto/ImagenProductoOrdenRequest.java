package com.esam.esam_backend.dto.imagenProducto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

// Cuerpo de PUT /api/productos/{sku}/imagenes/orden.
// La lista debe contener exactamente todas las imágenes del producto.
@Data
@NoArgsConstructor
public class ImagenProductoOrdenRequest {

    @NotEmpty(message = "La lista de imágenes no puede estar vacía")
    @Valid
    private List<Item> imagenes;

    @Data
    @NoArgsConstructor
    public static class Item {

        @NotNull(message = "El id de la imagen es obligatorio")
        @Min(value = 1, message = "El id de la imagen debe ser mayor que cero")
        private Long idImagenProducto;

        @NotNull(message = "El orden es obligatorio")
        @Min(value = 0, message = "El orden no puede ser negativo")
        private Integer orden;
    }
}