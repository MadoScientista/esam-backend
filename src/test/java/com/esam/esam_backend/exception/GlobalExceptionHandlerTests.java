package com.esam.esam_backend.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    @Test
    void mapsProductoWithImagesToConflict() {
        var requestProducto = new MockHttpServletRequest("DELETE", "/api/productos/42");
        var response = handler.manejarProductoConImagenes(
                new ProductoConImagenesException("El producto 42 tiene 3 imagen(es) asociada(s)"), requestProducto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("PRODUCTO_CON_IMAGENES", response.getBody().code());
        assertEquals("/api/productos/42", response.getBody().path());
    }

    @Test
    void mapsImagenNotFoundToNotFound() {
        var requestImagen = new MockHttpServletRequest("DELETE", "/api/productos/42/imagenes/7");
        var response = handler.manejarImagenNoEncontrada(
                new ImagenNoEncontradaException("No existe una imagen con id 7 en el producto 42"), requestImagen);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("IMAGEN_NO_ENCONTRADA", response.getBody().code());
        assertEquals("/api/productos/42/imagenes/7", response.getBody().path());
    }

    @Test
    void mapsImagenInvalidaToBadRequest() {
        var requestImagen = new MockHttpServletRequest("POST", "/api/productos/42/imagenes");
        var response = handler.manejarImagenInvalida(
                new ImagenInvalidaException("Formato no permitido, se aceptan jpg, png y webp"), requestImagen);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("IMAGEN_INVALIDA", response.getBody().code());
        assertEquals("/api/productos/42/imagenes", response.getBody().path());
    }

    @Test
    void mapsArchivoMuyGrandeToBadRequest() {
        var requestImagen = new MockHttpServletRequest("POST", "/api/productos/42/imagenes");
        var response = handler.manejarArchivoMuyGrande(
                new MaxUploadSizeExceededException(5L * 1024 * 1024), requestImagen);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ARCHIVO_MUY_GRANDE", response.getBody().code());
    }

    @Test
    void mapsComunaNotFoundToNotFound() {
        var requestComuna = new MockHttpServletRequest("PUT", "/api/comunas/99");
        var response = handler.manejarComunaNoEncontrada(
                new ComunaNoEncontradaException("No existe una comuna con id 99"), requestComuna);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("COMUNA_NO_ENCONTRADA", response.getBody().code());
        assertEquals("/api/comunas/99", response.getBody().path());
    }

    @Test
    void mapsComunaInvalidaToBadRequest() {
        var requestComuna = new MockHttpServletRequest("POST", "/api/comunas");
        var response = handler.manejarComunaInvalida(
                new ComunaInvalidaException("No existe una región con id 999"), requestComuna);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("COMUNA_INVALIDA", response.getBody().code());
        assertEquals("/api/comunas", response.getBody().path());
    }

    @Test
    void mapsComunaWithUsersToConflict() {
        var requestComuna = new MockHttpServletRequest("DELETE", "/api/comunas/5");
        var response = handler.manejarComunaConUsuarios(
                new ComunaConUsuariosException("La comuna 5 tiene 3 usuario(s) asociado(s)"), requestComuna);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("COMUNA_CON_USUARIOS", response.getBody().code());
        assertEquals("/api/comunas/5", response.getBody().path());
    }

    @Test
    void mapsMarcaNotFoundToNotFound() {
        var requestMarca = new MockHttpServletRequest("PUT", "/api/marcas/7");
        var response = handler.manejarMarcaNoEncontrada(
                new MarcaNoEncontradaException("No existe una marca con id 7"), requestMarca);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("MARCA_NO_ENCONTRADA", response.getBody().code());
        assertEquals("/api/marcas/7", response.getBody().path());
    }

    @Test
    void mapsMarcaWithProductsToConflict() {
        var requestMarca = new MockHttpServletRequest("DELETE", "/api/marcas/3");
        var response = handler.manejarMarcaConProductos(
                new MarcaConProductosException("La marca 3 tiene 2 producto(s) asociado(s)"), requestMarca);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("MARCA_CON_PRODUCTOS", response.getBody().code());
        assertEquals("/api/marcas/3", response.getBody().path());
    }

    @Test
    void mapsRegionNotFoundToNotFound() {
        var requestRegion = new MockHttpServletRequest("PUT", "/api/regiones/12");
        var response = handler.manejarRegionNoEncontrada(
                new RegionNoEncontradaException("No existe una región con id 12"), requestRegion);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("REGION_NO_ENCONTRADA", response.getBody().code());
        assertEquals("/api/regiones/12", response.getBody().path());
    }

    @Test
    void mapsRegionWithDependenciesToConflict() {
        var requestRegion = new MockHttpServletRequest("DELETE", "/api/regiones/4");
        var response = handler.manejarRegionConDependencias(
                new RegionConDependenciasException("La región 4 tiene 22 comuna(s) y 0 usuario(s) asociado(s)"),
                requestRegion);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("REGION_CON_DEPENDENCIAS", response.getBody().code());
        assertEquals("/api/regiones/4", response.getBody().path());
    }

    @Test
    void mapsRolUsuarioNotFoundToNotFound() {
        var requestRol = new MockHttpServletRequest("PUT", "/api/roles/9");
        var response = handler.manejarRolUsuarioNoEncontrado(
                new RolUsuarioNoEncontradaException("No existe un rol de usuario con id 9"), requestRol);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("ROL_USUARIO_NO_ENCONTRADO", response.getBody().code());
        assertEquals("/api/roles/9", response.getBody().path());
    }

    @Test
    void mapsRolUsuarioWithUsersToConflict() {
        var requestRol = new MockHttpServletRequest("DELETE", "/api/roles/1");
        var response = handler.manejarRolUsuarioConUsuarios(
                new RolUsuarioConUsuariosException("El rol 1 tiene 4 usuario(s) asociado(s)"), requestRol);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("ROL_USUARIO_CON_USUARIOS", response.getBody().code());
        assertEquals("/api/roles/1", response.getBody().path());
    }

    @Test
    void mapsUsuarioNotFoundToNotFound() {
        var requestUsuario = new MockHttpServletRequest("PUT", "/api/usuarios/50");
        var response = handler.manejarUsuarioNoEncontrado(
                new UsuarioNoEncontradaException("No existe un usuario con id 50"), requestUsuario);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("USUARIO_NO_ENCONTRADO", response.getBody().code());
        assertEquals("/api/usuarios/50", response.getBody().path());
    }

    @Test
    void mapsUsuarioInvalidoToBadRequest() {
        var requestUsuario = new MockHttpServletRequest("POST", "/api/usuarios");
        var response = handler.manejarUsuarioInvalido(
                new UsuarioInvalidaException("No existe una comuna con id 999"), requestUsuario);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("USUARIO_INVALIDO", response.getBody().code());
        assertEquals("/api/usuarios", response.getBody().path());
    }
}