package com.esam.esam_backend.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.esam.esam_backend.dto.pedido.PedidoEstadoDTORequest;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.security.ApiAccessDeniedHandler;
import com.esam.esam_backend.security.ApiAuthenticationEntryPoint;
import com.esam.esam_backend.security.CustomUserDetails;
import com.esam.esam_backend.security.CustomUserDetailsService;
import com.esam.esam_backend.security.JwtAuthenticationFilter;
import com.esam.esam_backend.security.JwtService;
import com.esam.esam_backend.security.SecurityConfig;
import com.esam.esam_backend.service.PedidoService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(PedidoController.class)
@Import({ SecurityConfig.class, PedidoControllerSecurityTests.TestSecurityBeans.class })
class PedidoControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void adminYVendedorPuedenListarTodosLosPedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos/admin").with(user(principalDe("admin"))))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/pedidos/admin").with(user(principalDe("vendedor"))))
                .andExpect(status().isOk());

        verify(pedidoService, org.mockito.Mockito.times(2)).obtenerTodosParaAdmin();
    }

    @Test
    void adminYVendedorPuedenConsultarUnPedidoPorId() throws Exception {
        mockMvc.perform(get("/api/pedidos/admin/1").with(user(principalDe("admin"))))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/pedidos/admin/1").with(user(principalDe("vendedor"))))
                .andExpect(status().isOk());

        verify(pedidoService, org.mockito.Mockito.times(2)).obtenerPorIdParaAdmin(1L);
    }

    @Test
    void adminYVendedorPuedenCambiarElEstadoDeUnPedido() throws Exception {
        mockMvc.perform(put("/api/pedidos/admin/1/estado")
                .contentType("application/json").content("{\"estado\":\"CONFIRMADO\"}")
                .with(user(principalDe("admin"))))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/pedidos/admin/1/estado")
                .contentType("application/json").content("{\"estado\":\"CONFIRMADO\"}")
                .with(user(principalDe("vendedor"))))
                .andExpect(status().isOk());

        verify(pedidoService, org.mockito.Mockito.times(2))
                .cambiarEstado(org.mockito.ArgumentMatchers.eq(1L),
                        org.mockito.ArgumentMatchers.any(Long.class),
                        org.mockito.ArgumentMatchers.any(PedidoEstadoDTORequest.class));
    }

    @Test
    void clienteNoPuedeAccederALaAdministracionDePedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos/admin").with(user(principalDe("cliente"))))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/pedidos/admin/1").with(user(principalDe("cliente"))))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/pedidos/admin/1/estado")
                .contentType("application/json").content("{\"estado\":\"CONFIRMADO\"}")
                .with(user(principalDe("cliente"))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pedidoService);
    }

    @Test
    void sinAutenticacionNoSeAccedeALaAdministracionDePedidos() throws Exception {
        mockMvc.perform(get("/api/pedidos/admin"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/pedidos/admin/1/estado")
                .contentType("application/json").content("{\"estado\":\"CONFIRMADO\"}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(pedidoService);
    }

    private CustomUserDetails principalDe(String rol) {
        RolUsuario rolUsuario = new RolUsuario();
        rolUsuario.setNombre(rol);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setRolUsuario(rolUsuario);
        return new CustomUserDetails(usuario);
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