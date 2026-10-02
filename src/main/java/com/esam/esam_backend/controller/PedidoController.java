package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esam.esam_backend.dto.pedido.PedidoDTORequest;
import com.esam.esam_backend.dto.pedido.PedidoDTOResponse;
import com.esam.esam_backend.dto.pedido.PedidoEstadoDTORequest;
import com.esam.esam_backend.dto.pedido.PedidoResumenDTOResponse;
import com.esam.esam_backend.security.CustomUserDetails;
import com.esam.esam_backend.service.PedidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoDTOResponse> crear(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid PedidoDTORequest request) {
        PedidoDTOResponse pedido = pedidoService.crear(
                principal.getUsuario().getIdUsuario(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResumenDTOResponse>> obtenerHistorial(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(
                pedidoService.obtenerHistorial(principal.getUsuario().getIdUsuario()));
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoDTOResponse> obtenerPorId(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long idPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(
                idPedido, principal.getUsuario().getIdUsuario()));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<PedidoDTOResponse>> obtenerTodosParaAdmin() {
        return ResponseEntity.ok(pedidoService.obtenerTodosParaAdmin());
    }

    @GetMapping("/admin/{idPedido}")
    public ResponseEntity<PedidoDTOResponse> obtenerPorIdParaAdmin(@PathVariable Long idPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPorIdParaAdmin(idPedido));
    }

    @PutMapping("/admin/{idPedido}/estado")
    public ResponseEntity<PedidoDTOResponse> cambiarEstado(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long idPedido,
            @RequestBody @Valid PedidoEstadoDTORequest request) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(
                idPedido, principal.getUsuario().getIdUsuario(), request));
    }
}
