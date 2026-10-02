package com.esam.esam_backend.enums;

public enum RolSistema {

    ADMIN("Administrador"),
    VENDEDOR("Vendedor"),
    CLIENTE("Cliente");

    String nombre;

    RolSistema(String nombre){
        this.nombre = nombre;
    }

    public String getNombre(){return this.nombre;}
    
}
