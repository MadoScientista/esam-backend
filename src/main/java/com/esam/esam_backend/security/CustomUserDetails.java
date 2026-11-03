package com.esam.esam_backend.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.esam.esam_backend.model.RolUsuario;
import com.esam.esam_backend.model.Usuario;

public class CustomUserDetails implements UserDetails{

    // Spring security trabajará a través de este usuario
    private final Usuario usuario;

    // Constructor de clase que recibe un usuario como argumento
    public CustomUserDetails(Usuario usuario){
        this.usuario = usuario;
    }

    // Acceso a la entidad que hay detrás, para los endpoints de perfil propio
    public Usuario getUsuario(){
        return usuario;
    }

    // Los métodos Override corresponden a métodos de la interfaz
    // UserDetails, por lo que se deben sobreescribir con la 
    // implementación dedicada al proyecto


    // Corresponde a la lista de permisos del usuario
    // Spring espera el prefijo "ROLE_" para que hasRole("admin") coincida
    // con el rol "admin". Sin rol asignado el usuario no tiene permisos.
    @Override 
    public Collection<? extends GrantedAuthority> getAuthorities(){
        RolUsuario rol = usuario.getRolUsuario();

        if (rol == null || !StringUtils.hasText(rol.getNombre())) {
            return List.of();
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
    }

    // Spring security usa "username" como identificador de usuario
    // Como en el modelo no hay username se usará el correo que es unique
    @Override 
    public String getUsername(){
        return usuario.getCorreo();
    }


    // Método para obtener la contraseña almacenada del usuario
    @Override 
    public String getPassword(){
        return usuario.getPassword() == null ? "" : usuario.getPassword();
    }


    // El modelo no distingue vencimiento de cuenta de su estado activo.
    @Override 
    public boolean isAccountNonExpired(){
        return true;
    }

    // No existe un estado de bloqueo independiente.
    @Override 
    public boolean isAccountNonLocked(){
        return true;
    }

    // Maneja estado de credencial expirada
    // Lo mismo de arriba
    @Override 
    public boolean isCredentialsNonExpired(){
        return true;
    }

    // Indica si el usuario está habilitado
    @Override 
    public boolean isEnabled(){
        return Boolean.TRUE.equals(usuario.getActivo());
    }

}




