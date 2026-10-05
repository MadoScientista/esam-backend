package com.esam.esam_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esam.esam_backend.dto.dashboard.DashboardResumenDTOResponse;
import com.esam.esam_backend.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTOResponse> obtenerResumen() {
        return ResponseEntity.ok(dashboardService.obtenerResumen());
    }
}
