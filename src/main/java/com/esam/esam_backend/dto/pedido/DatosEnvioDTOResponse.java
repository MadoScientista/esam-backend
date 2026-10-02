package com.esam.esam_backend.dto.pedido;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DatosEnvioDTOResponse {

    private String nombreReceptor;
    private String telefonoReceptor;
    private String calle;
    private String numero;
    private String complemento;
    private String comunaNombre;
    private String regionNombre;
}
