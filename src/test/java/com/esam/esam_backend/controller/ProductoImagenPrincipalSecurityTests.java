package com.esam.esam_backend.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.esam.esam_backend.dto.imagenProducto.ImagenProductoResponse;
import com.esam.esam_backend.mapper.ImagenProductoMapper;
import com.esam.esam_backend.mapper.ProductoMapper;
import com.esam.esam_backend.model.ImagenProducto;
import com.esam.esam_backend.security.ApiAccessDeniedHandler;
import com.esam.esam_backend.security.ApiAuthenticationEntryPoint;
import com.esam.esam_backend.security.CustomUserDetailsService;
import com.esam.esam_backend.security.JwtAuthenticationFilter;
import com.esam.esam_backend.security.JwtService;
import com.esam.esam_backend.security.SecurityConfig;
import com.esam.esam_backend.service.ImagenProductoService;
import com.esam.esam_backend.service.ProductoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ProductoController.class)
@Import({ SecurityConfig.class, ProductoImagenPrincipalSecurityTests.TestSecurityBeans.class })
class ProductoImagenPrincipalSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private ProductoMapper productoMapper;

    @MockitoBean
    private ImagenProductoService imagenProductoService;

    @MockitoBean
    private ImagenProductoMapper imagenProductoMapper;

    private ImagenProducto imagen;
    private ImagenProductoResponse respuesta;

    @BeforeEach
    void configurarRespuestas() {
        imagen = new ImagenProducto();
        imagen.setIdImagenProducto(11L);
        imagen.setPrincipal(true);
        respuesta = new ImagenProductoResponse();
        respuesta.setIdImagenProducto(11L);
        respuesta.setPrincipal(true);
        when(imagenProductoService.marcarPrincipal(7L, 11L)).thenReturn(imagen);
        when(imagenProductoMapper.toDTO(imagen)).thenReturn(respuesta);
    }

    @Test
    void vendedorNoPuedeCambiarImagenPrincipal() throws Exception {
        mockMvc.perform(put("/api/productos/7/imagenes/11/principal")
                .with(user("vendedor").roles("vendedor")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(imagenProductoService, imagenProductoMapper);
    }

    @Test
    void administradorPuedeCambiarImagenPrincipal() throws Exception {
        mockMvc.perform(put("/api/productos/7/imagenes/11/principal")
                .with(user("admin").roles("admin")))
                .andExpect(status().isOk());

        verify(imagenProductoService).marcarPrincipal(7L, 11L);
        verify(imagenProductoMapper).toDTO(imagen);
    }

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityBeans {

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(
                    org.mockito.Mockito.mock(JwtService.class),
                    org.mockito.Mockito.mock(CustomUserDetailsService.class)) {
                @Override
                protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain) throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }

        @Bean
        ApiAuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
            return new ApiAuthenticationEntryPoint(objectMapper);
        }

        @Bean
        ApiAccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
            return new ApiAccessDeniedHandler(objectMapper);
        }
    }
}