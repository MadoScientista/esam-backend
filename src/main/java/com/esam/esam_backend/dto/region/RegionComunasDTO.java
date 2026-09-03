package com.esam.esam_backend.dto.region;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegionComunasDTO {
    private Long idRegion;
    private String region;
    private List<ComunaDTO> comunas;

    @Data
    @NoArgsConstructor
    public static class ComunaDTO {
        private Long idComuna;
        private String nombre;
    }
}
