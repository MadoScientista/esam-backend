package com.esam.esam_backend.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.esam.esam_backend.dto.error.ApiErrorResponse;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Se invoca cuando la peticion no tiene una autenticacion valida.
// Devuelve 401 con el mismo formato de error que el resto de la API.
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public ApiAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
                HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException authException)
                throws IOException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "NO_AUTORIZADO",
                "Credenciales ausentes o token inválido o vencido",
                request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}