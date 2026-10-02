package com.esam.esam_backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class CustomUserDetailsService implements UserDetailsService{

    private final UsuarioRepository uRepo;

    // Implementación de métodos de la interfaz UserDetailsService


    // Método para obtener un usuario
    // Como se usa el correo como username se debe construir con cuidado
    @Override 
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException{

        // Busca el usuario en la base de datos
        Usuario usuario = uRepo.findByCorreo(correo)
                    .orElseThrow(() -> 
                        new UsernameNotFoundException(
                            "Usuario no encontrado"));
    

        // Convierte la entidad Usuario al objeto que Spring Security entiende.
        return new CustomUserDetails(usuario);
    }
}
