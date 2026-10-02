package com.esam.esam_backend.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.esam.esam_backend.dto.categoria.CategoriaDTORequest;
import com.esam.esam_backend.exception.CategoriaConProductosException;
import com.esam.esam_backend.exception.CategoriaConSubcategoriasException;
import com.esam.esam_backend.exception.CategoriaNoEncontradaException;
import com.esam.esam_backend.mapper.CategoriaMapper;
import com.esam.esam_backend.model.Categoria;
import com.esam.esam_backend.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private static final Pattern NON_ALNUMERIC = Pattern.compile("[^a-z0-9]+");

    private final CategoriaRepository cRepo;
    private final CategoriaMapper cMapper;

    public List<Categoria> obtenerCategorias() {
        return cRepo.findAll();
    }

    public List<Categoria> obtenerTodos() {
        return cRepo.findAll();
    }

    public Categoria obtenerCategoriaId(Long idCategoria) {
        return obtenerPorId(idCategoria);
    }

    public Categoria obtenerPorId(Long idCategoria) {
        return cRepo.findById(idCategoria).orElse(null);
    }

    public Categoria guardar(CategoriaDTORequest request) {
        Categoria padre = null;

        if (request.getIdCategoriaPadre() != null) {
            padre = obtenerCategoria(request.getIdCategoriaPadre());
        }

        validarRelacionPadre(null, padre);

        Categoria categoria = cMapper.toEntity(request, padre);
        categoria.setSlug(generarSlugUnico(request.getNombre(), null));

        return cRepo.save(categoria);
    }

    public Categoria editar(Long idCategoria, CategoriaDTORequest request) {
        Categoria categoria = obtenerCategoria(idCategoria);
        Categoria padre = null;

        if (request.getIdCategoriaPadre() != null) {
            padre = obtenerCategoria(request.getIdCategoriaPadre());
        }

        validarRelacionPadre(idCategoria, padre);

        categoria.setNombre(request.getNombre());
        categoria.setPadre(padre);
        categoria.setSlug(generarSlugUnico(request.getNombre(), idCategoria));

        return cRepo.save(categoria);
    }

    public void borrar(Long idCategoria) {
        Categoria categoria = obtenerCategoria(idCategoria);

        long subcategoriasAsociadas = cRepo.countByPadreIdCategoria(idCategoria);
        long productosAsociados = cRepo.countProductosAsociados(idCategoria);

        if (subcategoriasAsociadas > 0) {
            throw new CategoriaConSubcategoriasException(
                    "La categoría " + idCategoria + " tiene " + subcategoriasAsociadas
                            + " subcategoría(s) asociada(s)");
        }

        if (productosAsociados > 0) {
            throw new CategoriaConProductosException(
                    "La categoría " + idCategoria + " tiene " + productosAsociados
                            + " producto(s) asociado(s)");
        }

        cRepo.delete(categoria);
    }

    private Categoria obtenerCategoria(Long idCategoria) {
        Categoria categoria = obtenerPorId(idCategoria);

        if (categoria == null) {
            throw new CategoriaNoEncontradaException("No existe una categoría con id " + idCategoria);
        }

        return categoria;
    }

    public List<Categoria> obtenerRaices() {
        return cRepo.findAll().stream()
                .filter(categoria -> categoria.getPadre() == null)
                .toList();
    }

    public List<Categoria> obtenerPorPadre(Long idPadre) {
        return cRepo.findAll().stream()
                .filter(categoria -> categoria.getPadre() != null
                        && categoria.getPadre().getIdCategoria().equals(idPadre))
                .toList();
    }

    private void validarRelacionPadre(Long idCategoriaActual, Categoria padre) {
        if (padre == null) {
            return;
        }

        if (idCategoriaActual != null && idCategoriaActual.equals(padre.getIdCategoria())) {
            throw new IllegalArgumentException("Una categoría no puede ser su propio padre");
        }

        Categoria actual = padre;
        while (actual.getPadre() != null) {
            actual = actual.getPadre();
            if (idCategoriaActual != null && idCategoriaActual.equals(actual.getIdCategoria())) {
                throw new IllegalArgumentException("No se puede crear un ciclo en la jerarquía de categorías");
            }
        }
    }

    private String generarSlugUnico(String nombre, Long idCategoriaActual) {
        String baseSlug = generarSlug(nombre);
        String slug = baseSlug;
        int contador = 1;

        while (cRepo.existsBySlugAndIdCategoriaNot(slug, idCategoriaActual)) {
            slug = baseSlug + "-" + contador;
            contador++;
        }

        return slug;
    }

    private String generarSlug(String nombre) {
        String slug = Normalizer.normalize(nombre, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", "");
        slug = slug.toLowerCase(Locale.ROOT);
        slug = NON_ALNUMERIC.matcher(slug).replaceAll("-");
        slug = slug.replaceAll("-+", "-").replaceAll("^-|-$", "");

        if (slug.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede generar un slug válido");
        }

        return slug;
    }
}
