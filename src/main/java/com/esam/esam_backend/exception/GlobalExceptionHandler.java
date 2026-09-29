package com.esam.esam_backend.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> manejarValidacion(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return respuesta(HttpStatus.BAD_REQUEST, "ERROR_VALIDACION", message, request);
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> manejarErrorInesperado(
            Exception exception,
            HttpServletRequest request) {
        LOGGER.error("Error inesperado al procesar {}", request.getRequestURI(), exception);
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