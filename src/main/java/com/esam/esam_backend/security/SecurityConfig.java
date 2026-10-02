package com.esam.esam_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration 
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;
    private final ApiAccessDeniedHandler accessDeniedHandler;

    SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ApiAuthenticationEntryPoint authenticationEntryPoint,
            ApiAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // Obtiene el AuthenticationManager que Spring Scurity
    // configura internamente a partir de los componentes
    // de autenticación que están definidos en esta capa
    @Bean 
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration
    ) throws Exception{

        return configuration.getAuthenticationManager();
    }

    @Bean 
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http
    )throws Exception{
        http
        .csrf(csrf -> csrf.disable())  //JWT no utiliza sesiones http
        // Cada petición debe autenticarse mediante JWT
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        // 401 y 403 con el formato de error de la API
        .exceptionHandling(ex ->
            ex.authenticationEntryPoint(authenticationEntryPoint)
              .accessDeniedHandler(accessDeniedHandler)
        )
        // Reglas de acceso. El orden importa: gana el primer patron que
        // coincide, asi que las rutas concretas van antes que los comodines.
        .authorizeHttpRequests(auth -> auth
            // Catálogo público: cualquiera puede navegar productos, marcas,
            // regiones, comunas y roles sin token.
            .requestMatchers(HttpMethod.GET,
                "/api/productos", "/api/productos/**",
                "/api/marcas", "/api/marcas/**",
                "/api/regiones", "/api/regiones/**",
                "/api/comunas", "/api/comunas/**",
                "/api/roles", "/api/roles/**")
            .permitAll()

            // Login y registro público. El registro siempre crea un "cliente":
            // el rol no se puede elegir desde el cuerpo de la petición.
            .requestMatchers(HttpMethod.POST,
                "/api/usuarios/login",
                "/api/usuarios")
            .permitAll()

            // Perfil propio: cualquier usuario autenticado.
            .requestMatchers(HttpMethod.GET, "/api/usuarios/perfil")
            .authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/usuarios/perfil")
            .authenticated()

            // Vendedor y admin gestionan el catálogo.
            .requestMatchers(
                HttpMethod.POST,
                "/api/productos", "/api/productos/**")
            .hasAnyRole("admin", "vendedor")
            .requestMatchers(HttpMethod.PUT,
                "/api/productos/**")
            .hasAnyRole("admin", "vendedor")
            .requestMatchers(HttpMethod.DELETE,
                "/api/productos/**")
            .hasAnyRole("admin", "vendedor")

            // Marcas, geografía y roles: solo admin.
            .requestMatchers(
                HttpMethod.POST,
                "/api/marcas/**",
                "/api/regiones/**",
                "/api/comunas/**",
                "/api/roles/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.PUT,
                "/api/marcas/**",
                "/api/regiones/**",
                "/api/comunas/**",
                "/api/roles/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.DELETE,
                "/api/marcas/**",
                "/api/regiones/**",
                "/api/comunas/**",
                "/api/roles/**")
            .hasRole("admin")

            // Gestión de usuarios: solo admin.
            .requestMatchers(HttpMethod.POST, "/api/usuarios/admin")
            .hasRole("admin")
            .requestMatchers(HttpMethod.GET,
                "/api/usuarios",
                "/api/usuarios/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.PUT, "/api/usuarios/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**")
            .hasRole("admin")

            // Los clientes solo consultan sus pedidos desde el controlador;
            // las consultas globales y cambios de estado son exclusivos de admin.
            .requestMatchers(HttpMethod.GET,
                "/api/pedidos/admin", "/api/pedidos/admin/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.PUT, "/api/pedidos/admin/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.GET, "/api/pedidos/**")
            .authenticated()
            .requestMatchers(HttpMethod.POST, "/api/pedidos")
            .authenticated()

            .anyRequest()
            .authenticated()
        )
        // Ejecutar el filtro JWT antes del filtro estandar
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}