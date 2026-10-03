package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.esam.esam_backend.dto.direccion.DireccionDTO;
import com.esam.esam_backend.dto.direccion.DireccionDTORequest;
import com.esam.esam_backend.mapper.DireccionMapper;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.security.CustomUserDetails;
import com.esam.esam_backend.service.DireccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/direcciones")
public class DireccionController {

    private final DireccionService direccionService;
    private final DireccionMapper dMapper;

    @GetMapping
    public ResponseEntity<List<DireccionDTO>> obtenerTodos(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(dMapper.toDTOList(
                direccionService.obtenerPorUsuario(principal.getUsuario().getIdUsuario())));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<DireccionDTO>> obtenerPorUsuario(
            @PathVariable Long idUsuario,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Long idUsuarioAutenticado = principal.getUsuario().getIdUsuario();
        direccionService.validarPropietario(idUsuario, idUsuarioAutenticado);
        return ResponseEntity.ok(dMapper.toDTOList(direccionService.obtenerPorUsuario(idUsuarioAutenticado)));
    }

    @GetMapping("/usuario/{idUsuario}/activas")
    public ResponseEntity<List<DireccionDTO>> obtenerActivasPorUsuario(
            @PathVariable Long idUsuario,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Long idUsuarioAutenticado = principal.getUsuario().getIdUsuario();
        direccionService.validarPropietario(idUsuario, idUsuarioAutenticado);
        return ResponseEntity.ok(dMapper.toDTOList(
                direccionService.obtenerActivasPorUsuario(idUsuarioAutenticado)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DireccionDTO> obtenerPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Direccion direccion = direccionService.obtenerPorId(id, principal.getUsuario().getIdUsuario());
        if (direccion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dMapper.toDTO(direccion));
    }

    @PostMapping
    public ResponseEntity<DireccionDTO> guardar(
            @RequestBody @Valid DireccionDTORequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Direccion direccion = direccionService.guardar(request, principal.getUsuario().getIdUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(dMapper.toDTO(direccion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionDTO> editar(
            @PathVariable Long id,
            @RequestBody @Valid DireccionDTORequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Direccion direccion = direccionService.editar(
                id, request, principal.getUsuario().getIdUsuario());
        return ResponseEntity.ok(dMapper.toDTO(direccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        direccionService.borrar(id, principal.getUsuario().getIdUsuario());
        return ResponseEntity.noContent().build();
    }
}
