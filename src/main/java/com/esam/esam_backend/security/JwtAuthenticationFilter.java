package com.esam.esam_backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain)
                throws ServletException, IOException{

        // Si ya hay una autenticacion en el contexto no se vuelve a resolver
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtener el header Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no existe o no comienza con Bearer, continua con la petición
        if(authHeader == null || !authHeader.startsWith(PREFIJO_BEARER)){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraer únicamente el JWT
            String token = authHeader.substring(PREFIJO_BEARER.length());

            // Obtener el correo almacenado en el JWT
            String correo = jwtService.obtenerCorreo(token);

            // Buscar los datos completos del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(correo);

            // Crea la autenticación para Spring Security
            UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            // Asociar detalles de la petición
            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // Registrar al usuario como autenticado
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            // Token ausente, expirado, con firma invalida o de un usuario que ya
            // no existe. No se autentica y sigue la peticion: la autorizacion
            // respondera 401. Si no, la excepcion escaping de este filtro
            // devolveria 500, porque @RestControllerAdvice no la alcanza.
            SecurityContextHolder.clearContext();
        }

        // Continuar con la petición
        filterChain.doFilter(request, response);
    }
}