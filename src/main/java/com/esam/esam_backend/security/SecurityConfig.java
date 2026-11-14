package com.esam.esam_backend.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
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

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterServletRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
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
            // Documentación automática de OpenAPI y redirección a Swagger UI.
            .requestMatchers(
                "/api/swagger",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**")
            .permitAll()

            // Catálogo público: cualquiera puede navegar productos, categorías,
            // marcas, regiones, comunas y roles sin token.
            .requestMatchers(HttpMethod.GET,
                "/api/v1/productos", "/api/v1/productos/**",
                "/api/v1/categorias", "/api/v1/categorias/**",
                "/api/v1/marcas", "/api/v1/marcas/**",
                "/api/v1/regiones", "/api/v1/regiones/**",
                "/api/v1/comunas", "/api/v1/comunas/**",
                "/api/v1/roles", "/api/v1/roles/**")
            .permitAll()

            // Login y registro público. El registro siempre crea un "cliente":
            // el rol no se puede elegir desde el cuerpo de la petición.
            .requestMatchers(HttpMethod.POST,
                "/api/v1/usuarios/login",
                "/api/v1/usuarios/oauth/google",
                "/api/v1/usuarios")
            .permitAll()

            // Perfil propio: cualquier usuario autenticado.
            .requestMatchers(HttpMethod.GET, "/api/v1/usuarios/perfil")
            .authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/v1/usuarios/perfil")
            .authenticated()

            // Vendedor y admin gestionan el catálogo.
            .requestMatchers(
                HttpMethod.POST,
                "/api/v1/productos", "/api/v1/productos/**")
            .hasAnyRole("admin", "vendedor")
            // Solo admin puede cambiar cuál imagen se expone como principal.
            .requestMatchers(HttpMethod.PUT,
                "/api/v1/productos/*/imagenes/*/principal")
            .hasRole("admin")
            .requestMatchers(HttpMethod.PUT,
                "/api/v1/productos/**")
            .hasAnyRole("admin", "vendedor")
            .requestMatchers(HttpMethod.DELETE,
                "/api/v1/productos/**")
            .hasAnyRole("admin", "vendedor")

            // Solo admin puede crear, editar o eliminar categorías.
            .requestMatchers(HttpMethod.POST, "/api/v1/categorias", "/api/v1/categorias/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/categorias/**")
            .hasRole("admin")

            // Marcas, geografía y roles: solo admin.
            .requestMatchers(
                HttpMethod.POST,
                "/api/v1/marcas/**",
                "/api/v1/regiones/**",
                "/api/v1/comunas/**",
                "/api/v1/roles/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.PUT,
                "/api/v1/marcas/**",
                "/api/v1/regiones/**",
                "/api/v1/comunas/**",
                "/api/v1/roles/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.DELETE,
                "/api/v1/marcas/**",
                "/api/v1/regiones/**",
                "/api/v1/comunas/**",
                "/api/v1/roles/**")
            .hasRole("admin")

            // El resumen del dashboard está disponible para admin y vendedor.
            .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/**")
            .hasAnyRole("admin", "vendedor")

            // Gestión de usuarios: solo admin.
            .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/admin")
            .hasRole("admin")
            .requestMatchers(HttpMethod.GET,
                "/api/v1/usuarios",
                "/api/v1/usuarios/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.PUT, "/api/v1/usuarios/**")
            .hasRole("admin")
            .requestMatchers(HttpMethod.DELETE, "/api/v1/usuarios/**")
            .hasRole("admin")
            // Los clientes solo consultan sus pedidos desde el controlador;
            // las consultas globales y los cambios de estado son de admin y vendedor.
            .requestMatchers(HttpMethod.GET,
                "/api/v1/pedidos/admin", "/api/v1/pedidos/admin/**")
            .hasAnyRole("admin", "vendedor")
            .requestMatchers(HttpMethod.PUT,
                "/api/v1/pedidos/admin/**")
            .hasAnyRole("admin", "vendedor")
            .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/**")
            .authenticated()
            .requestMatchers(HttpMethod.POST, "/api/v1/pedidos")
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