package com.esam.esam_backend.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoogleOAuthRequest(
        @NotBlank
        @Size(max = 8192)
        String idToken) {
}
