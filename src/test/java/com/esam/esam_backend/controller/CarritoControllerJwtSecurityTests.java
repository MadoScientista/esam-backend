package com.esam.esam_backend.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.esam.esam_backend.dto.carrito.CarritoDTOResponse;
import com.esam.esam_backend.mapper.RolUsuarioMapper;
import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.security.ApiAccessDeniedHandler;
import com.esam.esam_backend.security.ApiAuthenticationEntryPoint;
import com.esam.esam_backend.security.CustomUserDetails;
import com.esam.esam_backend.security.CustomUserDetailsService;
import com.esam.esam_backend.security.JwtAuthenticationFilter;
import com.esam.esam_backend.security.JwtService;
import com.esam.esam_backend.security.SecurityConfig;
import com.esam.esam_backend.service.CarritoService;
import com.esam.esam_backend.service.RolUsuarioService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest({ CarritoController.class, RolUsuarioController.class })
@Import({ SecurityConfig.class, CarritoControllerJwtSecurityTests.TestSecurityBeans.class })
class CarritoControllerJwtSecurityTests {

    private static final Long ID_USUARIO = 42L;
    private static final JwtService JWT_SERVICE = crearJwtService();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private CarritoService carritoService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private RolUsuarioService rolUsuarioService;

    @MockitoBean
    private RolUsuarioMapper rolUsuarioMapper;

    private String token;

    @BeforeEach
    void configurarUsuarioAutenticado() {
        RolUsuario rol = new RolUsuario();
        rol.setNombre("cliente");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(ID_USUARIO);
        usuario.setCorreo("cliente@ejemplo.cl");
        usuario.setPassword("no-utilizada");
        usuario.setRolUsuario(rol);

        token = jwtService.generarToken(User.withUsername(usuario.getCorreo())
                .password("no-utilizada")
                .roles("cliente")
                .build());
        when(userDetailsService.loadUserByUsername(usuario.getCorreo()))
                .thenReturn(new CustomUserDetails(usuario));
        when(carritoService.obtenerOCrear(ID_USUARIO)).thenReturn(new CarritoDTOResponse());
        when(rolUsuarioService.obtenerTodos()).thenReturn(List.of());
        when(rolUsuarioMapper.toDTOList(List.of())).thenReturn(List.of());
    }

    @Test
    void jwtValidoPermiteAlClienteLlegarAlEndpointDeCarrito() throws Exception {
        mockMvc.perform(get("/api/carrito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        verify(userDetailsService).loadUserByUsername("cliente@ejemplo.cl");
        verify(carritoService).obtenerOCrear(ID_USUARIO);
    }

    @Test
    void jwtValidoNoBloqueaUnEndpointPublico() throws Exception {
        mockMvc.perform(get("/api/roles").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        verify(userDetailsService).loadUserByUsername("cliente@ejemplo.cl");
        verify(rolUsuarioService).obtenerTodos();
    }

    @Test
    void jwtConFirmaDistintaNoBloqueaUnEndpointPublico() throws Exception {
        JwtService otroEmisor = crearJwtService();
        String tokenConOtraFirma = otroEmisor.generarToken(
                User.withUsername("cliente@ejemplo.cl")
                        .password("no-utilizada")
                        .roles("cliente")
                        .build());

        mockMvc.perform(get("/api/roles")
                .header("Authorization", "Bearer " + tokenConOtraFirma))
                .andExpect(status().isOk());

        verify(rolUsuarioService).obtenerTodos();
    }

    @Test
    void jwtConFirmaDistintaEsRechazadoEnUnEndpointProtegido() throws Exception {
        JwtService otroEmisor = crearJwtService();
        String tokenConOtraFirma = otroEmisor.generarToken(
                User.withUsername("cliente@ejemplo.cl")
                        .password("no-utilizada")
                        .roles("cliente")
                        .build());

        mockMvc.perform(get("/api/carrito")
                .header("Authorization", "Bearer " + tokenConOtraFirma))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(carritoService);
    }

    @Test
    void sinJwtElEndpointProtegidoNoLlegaAlControlador() throws Exception {
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(carritoService);
    }

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityBeans {

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter(
                JwtService jwtService,
                CustomUserDetailsService userDetailsService) {
            return new JwtAuthenticationFilter(jwtService, userDetailsService);
        }

        @Bean
        JwtService jwtService() {
            return JWT_SERVICE;
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

    private static JwtService crearJwtService() {
        byte[] secret = new byte[32];
        new SecureRandom().nextBytes(secret);
        return new JwtService(Base64.getEncoder().encodeToString(secret), 3_600_000L);
    }
}
