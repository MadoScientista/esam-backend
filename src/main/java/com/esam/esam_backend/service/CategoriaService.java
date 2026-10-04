package com.esam.esam_backend.service;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.esam.esam_backend.dto.categoria.CategoriaDTORequest;
import com.esam.esam_backend.exception.CategoriaConProductosException;
import com.esam.esam_backend.exception.CategoriaConSubcategoriasException;
import com.esam.esam_backend.exception.CategoriaNoEncontradaException;
import com.esam.esam_backend.exception.ImagenInvalidaException;
import com.esam.esam_backend.mapper.CategoriaMapper;
import com.esam.esam_backend.model.Categoria;
import com.esam.esam_backend.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private static final Pattern NON_ALNUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final long TAMANO_MAXIMO_IMAGEN = 5L * 1024 * 1024;
    private static final List<String> TIPOS_IMAGEN_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp");

    private final CategoriaRepository cRepo;
    private final CategoriaMapper cMapper;
    private final Cloudinary cloudinary;

    public List<Categoria> obtenerTodos() {
        return cRepo.findAll();
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

    public Categoria guardarImagen(Long idCategoria, MultipartFile file) {
        validarImagen(file);
        Categoria categoria = obtenerCategoria(idCategoria);
        String idPublico = categoria.getImagenIdPublico() != null
                ? categoria.getImagenIdPublico()
                : "categorias/" + idCategoria + "/imagen";

        Map<?, ?> subida;
        try {
            subida = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", idPublico,
                            "overwrite", true,
                            "invalidate", true,
                            "resource_type", "image",
                            "allowed_formats", "jpg,jpeg,png,webp"));
        } catch (IOException | RuntimeException exception) {
            throw new ImagenInvalidaException("No se pudo subir la imagen de la categoría a Cloudinary");
        }

        if (subida == null) {
            throw new ImagenInvalidaException("Cloudinary no devolvió los datos necesarios de la imagen");
        }

        Object url = subida.get("secure_url");
        Object idDevuelto = subida.get("public_id");
        if (!(url instanceof String) || !(idDevuelto instanceof String)) {
            throw new ImagenInvalidaException("Cloudinary no devolvió los datos necesarios de la imagen");
        }

        categoria.setImagenUrl((String) url);
        categoria.setImagenIdPublico((String) idDevuelto);
        return cRepo.save(categoria);
    }

    public void borrarImagen(Long idCategoria) {
        Categoria categoria = obtenerCategoria(idCategoria);
        String idPublico = categoria.getImagenIdPublico();

        if (idPublico == null) {
            return;
        }

        Map<?, ?> resultado;
        try {
            resultado = cloudinary.uploader().destroy(idPublico, ObjectUtils.emptyMap());
        } catch (IOException | RuntimeException exception) {
            throw new ImagenInvalidaException("No se pudo eliminar la imagen de Cloudinary");
        }

        Object estado = resultado == null ? null : resultado.get("result");
        if (!"ok".equals(estado) && !"not found".equals(estado)) {
            throw new ImagenInvalidaException("No se pudo eliminar la imagen de Cloudinary");
        }

        categoria.setImagenUrl(null);
        categoria.setImagenIdPublico(null);
        cRepo.save(categoria);
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

        borrarImagen(idCategoria);
        cRepo.delete(categoria);
    }

    private Categoria obtenerCategoria(Long idCategoria) {
        Categoria categoria = obtenerPorId(idCategoria);

        if (categoria == null) {
            throw new CategoriaNoEncontradaException("No existe una categoría con id " + idCategoria);
        }

        return categoria;
    }

    private void validarImagen(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImagenInvalidaException("El archivo está vacío");
        }
        if (file.getSize() > TAMANO_MAXIMO_IMAGEN) {
            throw new ImagenInvalidaException("La imagen supera el tamaño máximo de 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !TIPOS_IMAGEN_PERMITIDOS.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ImagenInvalidaException("Formato no permitido, se aceptan jpg, png y webp");
        }
    }

    public List<Categoria> obtenerRaices() {
        return cRepo.findByPadreIsNull();
    }

    public List<Categoria> obtenerPorPadre(Long idPadre) {
        return cRepo.findByPadre_IdCategoria(idPadre);
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
