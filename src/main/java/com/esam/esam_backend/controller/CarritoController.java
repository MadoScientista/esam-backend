package com.esam.esam_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esam.esam_backend.dto.carrito.CarritoDTOResponse;
import com.esam.esam_backend.dto.carrito.ItemCarritoCantidadDTORequest;
import com.esam.esam_backend.dto.carrito.ItemCarritoDTORequest;
import com.esam.esam_backend.security.CustomUserDetails;
import com.esam.esam_backend.service.CarritoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoDTOResponse> obtener(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(carritoService.obtenerOCrear(principal.getUsuario().getIdUsuario()));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoDTOResponse> agregarItem(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid ItemCarritoDTORequest request) {
        return ResponseEntity.ok(
                carritoService.agregarItem(principal.getUsuario().getIdUsuario(), request));
    }

    @PutMapping("/items/{idProducto}")
    public ResponseEntity<CarritoDTOResponse> cambiarCantidad(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long idProducto,
            @RequestBody @Valid ItemCarritoCantidadDTORequest request) {
        return ResponseEntity.ok(carritoService.cambiarCantidad(
                principal.getUsuario().getIdUsuario(), idProducto, request));
    }

    @DeleteMapping("/items/{idProducto}")
    public ResponseEntity<CarritoDTOResponse> quitarItem(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long idProducto) {
        return ResponseEntity.ok(
                carritoService.quitarItem(principal.getUsuario().getIdUsuario(), idProducto));
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciar(
            @AuthenticationPrincipal CustomUserDetails principal) {
        carritoService.vaciar(principal.getUsuario().getIdUsuario());
        return ResponseEntity.noContent().build();
    }
}
