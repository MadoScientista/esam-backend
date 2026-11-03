package com.esam.esam_backend.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

class GoogleIdTokenVerifierTests {

    @Test
    void aceptaClaimsValidosDeGoogleParaLaAudienciaConfigurada() {
        NimbusJwtDecoder decoder = mock(NimbusJwtDecoder.class);
        new GoogleIdTokenVerifier("web-client-id", decoder);

        ArgumentCaptor<OAuth2TokenValidator<Jwt>> validatorCaptor =
                ArgumentCaptor.forClass(OAuth2TokenValidator.class);
        verify(decoder).setJwtValidator(validatorCaptor.capture());

        OAuth2TokenValidatorResult result =
                validatorCaptor.getValue().validate(jwtDeGoogle("web-client-id", true));

        assertFalse(result.hasErrors());
    }

    @Test
    void rechazaClaimsSinAudienciaCorrectaOCorreoVerificado() {
        NimbusJwtDecoder decoder = mock(NimbusJwtDecoder.class);
        new GoogleIdTokenVerifier("web-client-id", decoder);

        ArgumentCaptor<OAuth2TokenValidator<Jwt>> validatorCaptor =
                ArgumentCaptor.forClass(OAuth2TokenValidator.class);
        verify(decoder).setJwtValidator(validatorCaptor.capture());
        OAuth2TokenValidator<Jwt> validator = validatorCaptor.getValue();

        assertTrue(validator.validate(jwtDeGoogle("otro-client-id", true)).hasErrors());
        assertTrue(validator.validate(jwtDeGoogle("web-client-id", false)).hasErrors());
        assertTrue(validator.validate(jwtDeGoogle("web-client-id", true,
                Instant.now().minusSeconds(300))).hasErrors());
    }

    @Test
    void siFaltaElClientIdNoIntentaValidarToken() {
        GoogleIdTokenVerifier verifier =
                new GoogleIdTokenVerifier("", mock(NimbusJwtDecoder.class));

        assertThrows(ConfiguracionGoogleException.class, () -> verifier.verificar("id-token"));
    }

    private static Jwt jwtDeGoogle(String audience, boolean emailVerified) {
        return jwtDeGoogle(audience, emailVerified, Instant.now().plusSeconds(300));
    }

    private static Jwt jwtDeGoogle(String audience, boolean emailVerified, Instant expiresAt) {
        Instant ahora = Instant.now();
        Instant issuedAt = expiresAt.isBefore(ahora)
                ? expiresAt.minusSeconds(60)
                : ahora.minusSeconds(30);
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuer("https://accounts.google.com")
                .subject("google-subject")
                .audience(List.of(audience))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("email", "usuario@ejemplo.cl")
                .claim("email_verified", emailVerified)
                .build();
    }
}
