package com.esam.esam_backend.security;

public record GoogleIdentity(
        String subject,
        String email,
        String givenName,
        String familyName) {
}
