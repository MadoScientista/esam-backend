package com.esam.esam_backend.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Region;

class ComunaMapperTests {

    private final ComunaMapper mapper = new ComunaMapper();

    @Test
    void mapsRegionIdIntoComunaResponse() {
        Region region = new Region();
        region.setIdRegion(7L);

        Comuna comuna = new Comuna();
        comuna.setIdComuna(104L);
        comuna.setNombre("Santiago");
        comuna.setRegion(region);

        assertEquals(7L, mapper.toDTO(comuna).getIdRegion());
    }
}
