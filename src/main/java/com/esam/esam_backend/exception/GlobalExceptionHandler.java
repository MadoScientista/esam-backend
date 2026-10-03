package com.esam.esam_backend.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.esam.esam_backend.dto.error.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> manejarProductoNoEncontrado(
            ProductoNoEncontradoException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "PRODUCTO_NO_ENCONTRADO", exception.getMessage(), request);
    }

    @ExceptionHandler(ProductoInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> manejarProductoInvalido(
            ProductoInvalidoException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "PRODUCTO_INVALIDO", exception.getMessage(), request);
    }

    @ExceptionHandler(ConflictoStockException.class)
    public ResponseEntity<ApiErrorResponse> manejarConflictoStock(
            ConflictoStockException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "CONFLICTO_STOCK", exception.getMessage(), request);
    }

    @ExceptionHandler(ProductoConImagenesException.class)
    public ResponseEntity<ApiErrorResponse> manejarProductoConImagenes(
            ProductoConImagenesException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "PRODUCTO_CON_IMAGENES", exception.getMessage(), request);
    }

    @ExceptionHandler(ImagenNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarImagenNoEncontrada(
            ImagenNoEncontradaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "IMAGEN_NO_ENCONTRADA", exception.getMessage(), request);
    }

    @ExceptionHandler(ImagenInvalidaException.class)
    public ResponseEntity<ApiErrorResponse> manejarImagenInvalida(
            ImagenInvalidaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "IMAGEN_INVALIDA", exception.getMessage(), request);
    }

    // El archivo supera spring.servlet.multipart.max-file-size
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> manejarArchivoMuyGrande(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "ARCHIVO_MUY_GRANDE",
                "El archivo supera el tamaño máximo permitido", request);
    }

    @ExceptionHandler(ComunaNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarComunaNoEncontrada(
            ComunaNoEncontradaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "COMUNA_NO_ENCONTRADA", exception.getMessage(), request);
    }

    @ExceptionHandler(ComunaInvalidaException.class)
    public ResponseEntity<ApiErrorResponse> manejarComunaInvalida(
            ComunaInvalidaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "COMUNA_INVALIDA", exception.getMessage(), request);
    }

    @ExceptionHandler(ComunaConUsuariosException.class)
    public ResponseEntity<ApiErrorResponse> manejarComunaConUsuarios(
            ComunaConUsuariosException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "COMUNA_CON_USUARIOS", exception.getMessage(), request);
    }

    @ExceptionHandler(DireccionNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarDireccionNoEncontrada(
            DireccionNoEncontradaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "DIRECCION_NO_ENCONTRADA", exception.getMessage(), request);
    }

    @ExceptionHandler(DireccionInvalidaException.class)
    public ResponseEntity<ApiErrorResponse> manejarDireccionInvalida(
            DireccionInvalidaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "DIRECCION_INVALIDA", exception.getMessage(), request);
    }

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> manejarPedidoNoEncontrado(
            PedidoNoEncontradoException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "PEDIDO_NO_ENCONTRADO", exception.getMessage(), request);
    }

    @ExceptionHandler(PedidoInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> manejarPedidoInvalido(
            PedidoInvalidoException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "PEDIDO_INVALIDO", exception.getMessage(), request);
    }

    @ExceptionHandler(MarcaNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarMarcaNoEncontrada(
            MarcaNoEncontradaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "MARCA_NO_ENCONTRADA", exception.getMessage(), request);
    }

    @ExceptionHandler(MarcaConProductosException.class)
    public ResponseEntity<ApiErrorResponse> manejarMarcaConProductos(
            MarcaConProductosException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "MARCA_CON_PRODUCTOS", exception.getMessage(), request);
    }

    @ExceptionHandler(CategoriaNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarCategoriaNoEncontrada(
            CategoriaNoEncontradaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "CATEGORIA_NO_ENCONTRADA", exception.getMessage(), request);
    }

    @ExceptionHandler(CategoriaConProductosException.class)
    public ResponseEntity<ApiErrorResponse> manejarCategoriaConProductos(
            CategoriaConProductosException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "CATEGORIA_CON_PRODUCTOS", exception.getMessage(), request);
    }

    @ExceptionHandler(CategoriaConSubcategoriasException.class)
    public ResponseEntity<ApiErrorResponse> manejarCategoriaConSubcategorias(
            CategoriaConSubcategoriasException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "CATEGORIA_CON_SUBCATEGORIAS", exception.getMessage(), request);
    }

    @ExceptionHandler(RegionNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarRegionNoEncontrada(
            RegionNoEncontradaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "REGION_NO_ENCONTRADA", exception.getMessage(), request);
    }

    @ExceptionHandler(RegionConDependenciasException.class)
    public ResponseEntity<ApiErrorResponse> manejarRegionConDependencias(
            RegionConDependenciasException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "REGION_CON_DEPENDENCIAS", exception.getMessage(), request);
    }

    @ExceptionHandler(RolUsuarioNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarRolUsuarioNoEncontrado(
            RolUsuarioNoEncontradaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "ROL_USUARIO_NO_ENCONTRADO", exception.getMessage(), request);
    }

    @ExceptionHandler(RolUsuarioConUsuariosException.class)
    public ResponseEntity<ApiErrorResponse> manejarRolUsuarioConUsuarios(
            RolUsuarioConUsuariosException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "ROL_USUARIO_CON_USUARIOS", exception.getMessage(), request);
    }

    @ExceptionHandler(RolUsuarioDuplicadoException.class)
    public ResponseEntity<ApiErrorResponse> manejarRolUsuarioDuplicado(
            RolUsuarioDuplicadoException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "ROL_USUARIO_DUPLICADO", exception.getMessage(), request);
    }

    @ExceptionHandler(RolUsuarioSistemaException.class)
    public ResponseEntity<ApiErrorResponse> manejarRolUsuarioSistema(
            RolUsuarioSistemaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "ROL_USUARIO_SISTEMA", exception.getMessage(), request);
    }

    @ExceptionHandler(UsuarioNoEncontradaException.class)
    public ResponseEntity<ApiErrorResponse> manejarUsuarioNoEncontrado(
            UsuarioNoEncontradaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "USUARIO_NO_ENCONTRADO", exception.getMessage(), request);
    }

    @ExceptionHandler(UsuarioInvalidaException.class)
    public ResponseEntity<ApiErrorResponse> manejarUsuarioInvalido(
            UsuarioInvalidaException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "USUARIO_INVALIDO", exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> manejarValidacion(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respuesta(HttpStatus.BAD_REQUEST, "ERROR_VALIDACION", message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> manejarArgumentoInvalido(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "ARGUMENTO_INVALIDO", exception.getMessage(), request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiErrorResponse> manejarParametroInvalido(
            Exception exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "PARAMETRO_INVALIDO", "Uno o más parámetros de la solicitud no son válidos", request);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiErrorResponse> manejarErrorHttp(
            ErrorResponse exception,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        String message = exception.getBody().getDetail();
        if (message == null || message.isBlank()) {
            message = status.getReasonPhrase();
        }
        return respuesta(status, "ERROR_HTTP", message, request);
    }

    // Una restricción de integridad violada trae el valor que la incumplió en el
    // mensaje de MySQL (por ejemplo el correo duplicado), así que la excepción no
    // se loguea: se registra la ruta y se responde con un mensaje genérico.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> manejarConflictoDatos(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        LOGGER.warn("Restricción de integridad violada al procesar {}", request.getRequestURI());
        return respuesta(HttpStatus.CONFLICT, "CONFLICTO_DATOS",
                "La operación viola una restricción de integridad de la base de datos", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> manejarAccesoDenegado(
            AccessDeniedException exception,
            HttpServletRequest request) {
        return respuesta(HttpStatus.FORBIDDEN, "ACCESO_DENEGADO",
                "No tiene permiso para realizar esta operación", request);
    }

    // Tampoco se adjunta la excepción: su mensaje puede contener datos del
    // usuario. Se registra el tipo, que basta para clasificar el fallo.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> manejarErrorInesperado(
            Exception exception,
            HttpServletRequest request) {
        LOGGER.error("Error inesperado al procesar {} ({})",
                request.getRequestURI(),
                exception.getClass().getSimpleName());
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", "Ocurrió un error interno", request);
    }

    private ResponseEntity<ApiErrorResponse> respuesta(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}