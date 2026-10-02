package com.esam.esam_backend.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.error.ApiErrorResponse;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Se invoca cuando el usuario esta autenticado pero su rol no alcanza para
// la peticion. Devuelve 403 con el mismo formato de error que el resto de la API.
@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public ApiAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                AccessDeniedException accessDeniedException)
                throws IOException {

        HttpStatus status = HttpStatus.FORBIDDEN;

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "ACCESO_DENEGADO",
                "El rol del usuario no tiene permisos para esta operación",
                request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}