package com.esam.esam_backend.dto.categoria;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoriaDTO {

    private Long idCategoria;
    private String nombre;
    private String slug;
    private Long idCategoriaPadre;
}