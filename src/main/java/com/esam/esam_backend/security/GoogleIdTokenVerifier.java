package com.esam.esam_backend.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class GoogleIdTokenVerifier {

    private static final String GOOGLE_JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final List<String> GOOGLE_ISSUERS = List.of(
            "https://accounts.google.com",
            "accounts.google.com");

    private final String clientId;
    private final NimbusJwtDecoder jwtDecoder;

    @Autowired
    public GoogleIdTokenVerifier(@Value("${google.oauth.client-id:}") String clientId) {
        this(clientId, NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWK_SET_URI).build());
    }

    GoogleIdTokenVerifier(String clientId, NimbusJwtDecoder jwtDecoder) {
        this.clientId = clientId;
        this.jwtDecoder = jwtDecoder;
        OAuth2TokenValidator<Jwt> googleClaimsValidator = jwt -> {
            String issuer = jwt.getClaimAsString("iss");
            String authorizedParty = jwt.getClaimAsString("azp");
            boolean audienceValid = StringUtils.hasText(clientId)
                    && jwt.getAudience().contains(clientId)
                    && (authorizedParty == null || clientId.equals(authorizedParty))
                    && (jwt.getAudience().size() == 1 || clientId.equals(authorizedParty));
            boolean claimsValid = GOOGLE_ISSUERS.contains(issuer)
                    && audienceValid
                    && StringUtils.hasText(jwt.getSubject())
                    && jwt.getExpiresAt() != null
                    && StringUtils.hasText(jwt.getClaimAsString("email"))
                    && Boolean.TRUE.equals(jwt.getClaim("email_verified"));
            return claimsValid
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        };
        this.jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                googleClaimsValidator));
    }

    public GoogleIdentity verificar(String idToken) {
        if (!StringUtils.hasText(clientId)) {
            throw new ConfiguracionGoogleException();
        }

        try {
            Jwt jwt = jwtDecoder.decode(idToken);
            return new GoogleIdentity(
                    jwt.getSubject(),
                    jwt.getClaimAsString("email"),
                    jwt.getClaimAsString("given_name"),
                    jwt.getClaimAsString("family_name"));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AutenticacionGoogleException();
        }
    }
}
