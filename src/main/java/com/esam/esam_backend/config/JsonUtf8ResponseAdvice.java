package com.esam.esam_backend.config;

import java.nio.charset.StandardCharsets;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class JsonUtf8ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        String subtype = selectedContentType.getSubtype();
        if (MediaType.APPLICATION_JSON.isCompatibleWith(selectedContentType)
                || subtype.endsWith("+json")) {
            response.getHeaders().setContentType(new MediaType(selectedContentType, StandardCharsets.UTF_8));
        }
        return body;
    }
}
