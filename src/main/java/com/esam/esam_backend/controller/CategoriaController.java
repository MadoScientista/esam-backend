package com.esam.esam_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.esam.esam_backend.dto.categoria.CategoriaDTO;
import com.esam.esam_backend.dto.categoria.CategoriaDTORequest;
import com.esam.esam_backend.mapper.CategoriaMapper;
import com.esam.esam_backend.model.Categoria;
import com.esam.esam_backend.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaMapper cMapper;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> obtenerTodos() {
        return ResponseEntity.ok(cMapper.toDTOList(categoriaService.obtenerTodos()));
    }

    @GetMapping("/raiz")
    public ResponseEntity<List<CategoriaDTO>> obtenerRaices() {
        return ResponseEntity.ok(cMapper.toDTOList(categoriaService.obtenerRaices()));
    }

    @GetMapping("/padre/{idPadre}")
    public ResponseEntity<List<CategoriaDTO>> obtenerPorPadre(@PathVariable Long idPadre) {
        return ResponseEntity.ok(cMapper.toDTOList(categoriaService.obtenerPorPadre(idPadre)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obtenerPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.obtenerPorId(id);

        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cMapper.toDTO(categoria));
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> guardar(@RequestBody @Valid CategoriaDTORequest request) {
        Categoria categoria = categoriaService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cMapper.toDTO(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> editar(@PathVariable Long id, @RequestBody @Valid CategoriaDTORequest request) {
        Categoria categoria = categoriaService.editar(id, request);
        return ResponseEntity.ok(cMapper.toDTO(categoria));
    }

    @PostMapping("/{id}/imagen")
    public ResponseEntity<CategoriaDTO> guardarImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        Categoria categoria = categoriaService.guardarImagen(id, file);
        return ResponseEntity.ok(cMapper.toDTO(categoria));
    }

    @DeleteMapping("/{id}/imagen")
    public ResponseEntity<Void> borrarImagen(@PathVariable Long id) {
        categoriaService.borrarImagen(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        categoriaService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
