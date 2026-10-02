package com.esam.esam_backend.dto.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CampoErrorDTOResponse {

    private String campo;
    private String mensaje;
}
