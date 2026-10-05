package com.esam.esam_backend.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.esam.esam_backend.security.ApiAccessDeniedHandler;
import com.esam.esam_backend.security.ApiAuthenticationEntryPoint;
import com.esam.esam_backend.security.CustomUserDetailsService;
import com.esam.esam_backend.security.JwtAuthenticationFilter;
import com.esam.esam_backend.security.JwtService;
import com.esam.esam_backend.security.SecurityConfig;
import com.esam.esam_backend.service.DashboardService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DashboardController.class)
@Import({ SecurityConfig.class, DashboardControllerSecurityTests.TestSecurityBeans.class })
class DashboardControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void administradorYVendedorPuedenConsultarElResumen() throws Exception {
        mockMvc.perform(get("/api/dashboard/resumen").with(user("admin").roles("admin")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/dashboard/resumen").with(user("vendedor").roles("vendedor")))
                .andExpect(status().isOk());

        verify(dashboardService, org.mockito.Mockito.times(2)).obtenerResumen();
    }

    @Test
    void clienteNoPuedeConsultarElResumen() throws Exception {
        mockMvc.perform(get("/api/dashboard/resumen").with(user("cliente").roles("cliente")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(dashboardService);
    }

    @Test
    void laConsultaSinAutenticacionSeRechaza() throws Exception {
        mockMvc.perform(get("/api/dashboard/resumen"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(dashboardService);
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
