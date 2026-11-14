package com.esam.esam_backend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping("/api/swagger")
    public String swagger() {
        return "redirect:/swagger-ui.html";
    }
}
