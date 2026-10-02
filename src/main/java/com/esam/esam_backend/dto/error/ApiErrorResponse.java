package com.esam.esam_backend.dto.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        String mensaje,
        String ruta,
        List<CampoErrorDTOResponse> errores) {

    public ApiErrorResponse(Instant timestamp, int status, String error, String code, String message, String path) {
        this(timestamp, status, error, code, message, path, message, path, List.of());
    }

    public ApiErrorResponse(Instant timestamp, int status, String error, String code, String message, String path,
            List<CampoErrorDTOResponse> errores) {
        this(timestamp, status, error, code, message, path, message, path, errores == null ? List.of() : errores);
    }
}