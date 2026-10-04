package com.esam.esam_backend.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.esam.esam_backend.dto.categoria.CategoriaDTO;
import com.esam.esam_backend.dto.categoria.CategoriaDTORequest;
import com.esam.esam_backend.mapper.CategoriaMapper;
import com.esam.esam_backend.model.Categoria;
import com.esam.esam_backend.security.ApiAccessDeniedHandler;
import com.esam.esam_backend.security.ApiAuthenticationEntryPoint;
import com.esam.esam_backend.security.CustomUserDetailsService;
import com.esam.esam_backend.security.JwtAuthenticationFilter;
import com.esam.esam_backend.security.JwtService;
import com.esam.esam_backend.security.SecurityConfig;
import com.esam.esam_backend.service.CategoriaService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@WebMvcTest(CategoriaController.class)
@Import({ SecurityConfig.class, CategoriaControllerSecurityTests.TestSecurityBeans.class })
class CategoriaControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private CategoriaMapper categoriaMapper;

    private Categoria categoria;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void configurarRespuestas() {
        categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombre("Categoria");
        categoria.setSlug("categoria");
        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setIdCategoria(1L);
        categoriaDTO.setNombre("Categoria");
        categoriaDTO.setSlug("categoria");
        when(categoriaService.guardar(org.mockito.ArgumentMatchers.any(CategoriaDTORequest.class)))
                .thenReturn(categoria);
        when(categoriaService.editar(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(CategoriaDTORequest.class))).thenReturn(categoria);
        when(categoriaService.guardarImagen(org.mockito.ArgumentMatchers.eq(1L),
            org.mockito.ArgumentMatchers.any(MultipartFile.class))).thenReturn(categoria);
        when(categoriaMapper.toDTO(categoria)).thenReturn(categoriaDTO);
    }

    @Test
    void clienteNoPuedeCrearCategorias() throws Exception {
        mockMvc.perform(post("/api/categorias").contentType("application/json")
                .content("{\"nombre\":\"Categoria\"}").with(user("cliente").roles("cliente")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(categoriaService, categoriaMapper);
    }

    @Test
    void vendedorNoPuedeEditarCategorias() throws Exception {
        mockMvc.perform(put("/api/categorias/1").contentType("application/json")
                .content("{\"nombre\":\"Categoria\"}").with(user("vendedor").roles("vendedor")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(categoriaService, categoriaMapper);
    }

    @Test
    void clienteNoPuedeEliminarCategorias() throws Exception {
        mockMvc.perform(delete("/api/categorias/1").with(user("cliente").roles("cliente")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(categoriaService, categoriaMapper);
    }

    @Test
    void administradorPuedeCrearEditarYEliminarCategorias() throws Exception {
        mockMvc.perform(post("/api/categorias").contentType("application/json")
                .content("{\"nombre\":\"Categoria\"}").with(user("admin").roles("admin")))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/categorias/1").contentType("application/json")
                .content("{\"nombre\":\"Categoria\"}").with(user("admin").roles("admin")))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/categorias/1").with(user("admin").roles("admin")))
                .andExpect(status().isNoContent());

        verify(categoriaService).guardar(org.mockito.ArgumentMatchers.any(CategoriaDTORequest.class));
        verify(categoriaService).editar(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(CategoriaDTORequest.class));
        verify(categoriaService).borrar(1L);
    }

    @Test
    void soloAdministradorPuedeGestionarImagenDeCategoria() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "categoria.png", "image/png", new byte[] { 1, 2, 3 });

        mockMvc.perform(multipart("/api/categorias/1/imagen")
                .file(file)
                .with(user("vendedor").roles("vendedor")))
                .andExpect(status().isForbidden());
        mockMvc.perform(multipart("/api/categorias/1/imagen")
                .file(file)
                .with(user("admin").roles("admin")))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/categorias/1/imagen")
                .with(user("admin").roles("admin")))
                .andExpect(status().isNoContent());

        verify(categoriaService).guardarImagen(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(MultipartFile.class));
        verify(categoriaService).borrarImagen(1L);
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
