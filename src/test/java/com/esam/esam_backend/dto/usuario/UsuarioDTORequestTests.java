package com.esam.esam_backend.dto.usuario;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;

class UsuarioDTORequestTests {

    @Test
    void putPermiteCamposOmitidosPeroCreacionExigeDatosObligatorios() {
        UsuarioDTORequest request = new UsuarioDTORequest();
        var validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertTrue(validator.validate(request).isEmpty());
        assertFalse(validator.validate(request, UsuarioDTORequest.Creacion.class).isEmpty());

        request.setCorreo(" ");
        assertFalse(validator.validate(request).isEmpty());
    }
}
