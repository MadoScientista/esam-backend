package com.esam.esam_backend.dto.productImage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductImageResponse {

    @Min(value = 1)
    private Long idProductImage;

    @NotBlank
    private String url;

    private boolean principal;
}
