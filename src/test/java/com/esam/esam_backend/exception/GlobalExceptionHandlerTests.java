package com.esam.esam_backend.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos/42");

    @Test
    void mapsProductNotFoundToNotFound() {
        var response = handler.manejarProductoNoEncontrado(
                new ProductoNoEncontradoException("No existe un producto con SKU 42"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("PRODUCTO_NO_ENCONTRADO", response.getBody().code());
        assertEquals("/api/productos/42", response.getBody().path());
    }

    @Test
    void mapsInvalidProductToBadRequest() {
        var response = handler.manejarProductoInvalido(
                new ProductoInvalidoException("El precio debe ser un número positivo o cero"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("PRODUCTO_INVALIDO", response.getBody().code());
    }

    @Test
    void mapsStockConflictToConflict() {
        var response = handler.manejarConflictoStock(
                new ConflictoStockException("No hay stock suficiente"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLICTO_STOCK", response.getBody().code());
    }
}