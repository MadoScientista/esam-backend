package com.esam.esam_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;


@Configuration 
public class CloudinaryConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryConfig.class);

    private static final String PREFIJO_ESPERADO = "cloudinary://";

    @Bean
    public Cloudinary cloudinary(@Value("${cloudinary.url}") String cloudinaryUrl){

        // Se valida el formato antes de llamar a new Cloudinary(String): el SDK
        // hace URI.create sobre el valor crudo y el URISyntaxException que
        // devuelve incluye la URL completa, con la API key y el API secret
        // adentro, en el mensaje. Ese mensaje termina en los logs.
        if (cloudinaryUrl == null || !cloudinaryUrl.startsWith(PREFIJO_ESPERADO)) {
            throw new IllegalStateException(
                    "cloudinary.url debe tener el formato cloudinary://<key>:<secret>@<cloud_name>");
        }

        try {
            return new Cloudinary(cloudinaryUrl);
        } catch (RuntimeException exception) {
            // El prefijo no garantiza que la URL sea parseable: un valor mal formado
            // que igual empieza con cloudinary:// hace fallar URI.create dentro del
            // constructor con el valor crudo en el mensaje. Se descarta la causa
            // para que la API key no llegue al log del arranque.
            LOGGER.error("No se pudo construir el bean de Cloudinary ({})",
                    exception.getClass().getSimpleName());
            throw new IllegalStateException("cloudinary.url no es una URL de Cloudinary válida");
        }
    }
}
