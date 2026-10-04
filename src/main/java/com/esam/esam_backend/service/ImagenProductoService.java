package com.esam.esam_backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.esam.esam_backend.exception.ImagenInvalidaException;
import com.esam.esam_backend.exception.ImagenNoEncontradaException;
import com.esam.esam_backend.exception.ProductoNoEncontradoException;
import com.esam.esam_backend.mapper.ImagenProductoMapper;
import com.esam.esam_backend.model.ImagenProducto;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.ImagenProductoRepository;
import com.esam.esam_backend.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ImagenProductoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagenProductoService.class);

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("image/jpeg", "image/png", "image/webp");

    private static final long TAMANO_MAXIMO = 5L * 1024 * 1024;

    private final Cloudinary cloudinary;

    private final ImagenProductoRepository ipRepo;

    private final ProductoRepository pRepo;

    private final ImagenProductoMapper ipMapper;


    // Guardar una imagen de un producto, subida a Cloudinary
    public ImagenProducto guardar(Long sku, MultipartFile file) {
        validarArchivo(file);

        Producto producto = pRepo.findById(sku)
            .orElseThrow(() -> new ProductoNoEncontradoException("No existe el producto " + sku));

        Map<?, ?> subida = subir(file, sku);

        String url = leerUrl(subida);
        String idPublico = (String) subida.get("public_id");


        // La imagen nueva se agrega al final de la galería.
        Integer orden = ipRepo.maxOrdenPorProducto(sku) + 1;

        ImagenProducto imagen = ipMapper.toEntity(url, idPublico, producto, orden);

        try {
            return ipRepo.save(imagen);
        } catch (RuntimeException exception) {
            // Si la fila no se persiste, el asset queda huerfano en Cloudinary
            destruirEnCloudinary(idPublico);
            throw exception;
        }
    }

    // Todas las imagenes de un producto, por orden de galeria.
    // Devuelve null si el producto no existe: el 404 lo decide el controller.
    public List<ImagenProducto> obtenerPorProducto(Long sku) {
        if (pRepo.findById(sku).isEmpty()) {
            return null;
        }

        return ipRepo.findByProductoSkuOrderByOrdenAsc(sku);
    }

    // Seleccionar la imagen principal de un producto. El bloqueo del producto
    // serializa cambios concurrentes para mantener una sola imagen principal.
    @Transactional
    public ImagenProducto marcarPrincipal(Long sku, Long idImagenProducto) {
        Producto producto = pRepo.buscarPorIdParaPedido(sku)
                .orElseThrow(() -> new ProductoNoEncontradoException("No existe el producto " + sku));

        List<ImagenProducto> imagenes = producto.getImagenes();
        ImagenProducto seleccionada = imagenes.stream()
                .filter(imagen -> idImagenProducto.equals(imagen.getIdImagenProducto()))
                .findFirst()
                .orElseThrow(() -> new ImagenNoEncontradaException(
                        "La imagen " + idImagenProducto + " no pertenece al producto " + sku));

        imagenes.forEach(imagen -> imagen.setPrincipal(imagen == seleccionada));
        ipRepo.saveAll(imagenes);

        return seleccionada;
    }

    // Borrar una imagen, en Cloudinary y en la base de datos
    public ImagenProducto borrar(Long sku, Long idImagenProducto) {
        ImagenProducto imagen = ipRepo.findById(idImagenProducto).orElse(null);

        if(imagen == null){
            return null;
        }
        // Si la destruccion falla la fila sobrevive para poder reintentar
        destruirEnCloudinary(imagen.getIdPublico());

        ipRepo.delete(imagen);

        return imagen;
    }


    // Borrar todas las imagenes de un producto.
    // Lo usan ProductoService.borrarEnCascada y MarcaService.borrarEnCascada
    @Transactional
    public void borrarTodas(Long sku) {
        List<ImagenProducto> imagenes = ipRepo.findByProductoSkuOrderByOrdenAsc(sku);

        for (ImagenProducto imagen : imagenes) {
            destruirEnCloudinary(imagen.getIdPublico());
        }

        ipRepo.deleteAll(imagenes);
    }

    // Cantidad de imagenes de un producto
    public long contarPorProducto(Long sku) {
        return ipRepo.countByProductoSku(sku);
    }



    private Map<?, ?> subir(MultipartFile file, Long sku) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "productos/" + sku,
                            "resource_type", "image",
                            "allowed_formats", "jpg,jpeg,png,webp"
                    )
            );
        } catch (IOException | RuntimeException exception) {
            // El SDK lanza RuntimeException con el mensaje "Invalid api_key <key>".
            // Ni el mensaje ni el stack trace se pueden volcar al log: se registra
            // solo el tipo de excepción. Tampoco se encadena como causa, porque
            // arrastraría el mensaje del SDK a los logs del contenedor de errores.
            LOGGER.error("Falló la subida de la imagen del producto {} a Cloudinary ({})",
                    sku, exception.getClass().getSimpleName());
            throw new ImagenInvalidaException("No se pudo subir la imagen a Cloudinary");
        }
    }

    private void destruirEnCloudinary(String idPublico) {
        Map<?, ?> resultado;

        try {
            resultado = cloudinary.uploader().destroy(idPublico, ObjectUtils.emptyMap());
        } catch (IOException | RuntimeException exception) {
            // Mismo criterio que en subir(): el mensaje del SDK puede traer el
            // api_key, así que al log solo va el tipo de excepción.
            LOGGER.error("Falló la eliminación del asset {} en Cloudinary ({})",
                    idPublico, exception.getClass().getSimpleName());
            throw new ImagenInvalidaException("No se pudo eliminar la imagen de Cloudinary");
        }

        Object estado = resultado == null ? null : resultado.get("result");

        if (!"ok".equals(estado) && !"not found".equals(estado)) {
            LOGGER.error("Cloudinary devolvió {} al eliminar el asset {}", estado, idPublico);
            throw new ImagenInvalidaException("No se pudo eliminar la imagen de Cloudinary");
        }
    }

    private String leerUrl(Map<?, ?> subida) {
        Object url = subida.get("secure_url");

        if (url == null) {
            url = subida.get("url");
        }

        if (url == null) {
            LOGGER.error("La respuesta de Cloudinary no trae secure_url ni url. Claves: {}", subida.keySet());
            throw new ImagenInvalidaException("Cloudinary no devolvió una URL para la imagen subida");
        }

        return (String) url;
    }

    private void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImagenInvalidaException("El archivo está vacío");
        }

        if (file.getSize() > TAMANO_MAXIMO) {
            throw new ImagenInvalidaException("La imagen supera el tamaño máximo de 5MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType.toLowerCase())) {
            throw new ImagenInvalidaException("Formato no permitido, se aceptan jpg, png y webp");
        }
    }
}
