package com.esam.esam_backend.security;


import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service 
public class JwtService {

    // Variable para almacenar la clave secreta
    // que firma y valida los JWT
    private final SecretKey secretKey;

    // Tiempo de duración del token, en milisegundos
    private final long expiration;

    //Constructor del service
    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expiration 
    ){
        // La clave se almacena en Base64 en la configuración
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
        
    }

    // Genera un JWT para el usuario autenticado
    public String generarToken(UserDetails userDetails){

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                // Identificador de usuario
                .subject(userDetails.getUsername())
                // Momento en el que se generó
                .issuedAt(ahora)
                // Momento en el que dejará de ser válido
                .expiration(expiracion)
                // Firma digital del token
                .signWith(secretKey)
                // Construye el token
                .compact();
    }


    // Obtiene el correo almacenado dentro del JWT
    public String obtenerCorreo(String token){
        
        return obtenerClaims(token).getSubject();
    }

    // Obtiene la información "claims" contenida en el JWT
    private Claims obtenerClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
