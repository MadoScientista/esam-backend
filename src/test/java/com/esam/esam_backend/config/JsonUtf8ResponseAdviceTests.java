package com.esam.esam_backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;

class JsonUtf8ResponseAdviceTests {

    @Test
    void addsUtf8CharsetToJsonResponses() {
        var servletResponse = new MockHttpServletResponse();
        var response = new ServletServerHttpResponse(servletResponse);

        new JsonUtf8ResponseAdvice().beforeBodyWrite(
                "á",
                null,
                MediaType.APPLICATION_JSON,
                null,
                null,
                response);

        assertEquals(
                new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8),
                response.getHeaders().getContentType());
    }
}
