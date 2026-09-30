package com.esam.esam_backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.esam.esam_backend.exception.ImagenInvalidaException;
import com.esam.esam_backend.exception.ImagenNoEncontradaException;
import com.esam.esam_backend.exception.ProductoNoEncontradoException;
import com.esam.esam_backend.mapper.ProductImageMapper;
import com.esam.esam_backend.model.ProductImage;
import com.esam.esam_backend.model.Producto;
import com.esam.esam_backend.repository.ProductImageRepository;
import com.esam.esam_backend.repository.ProductoRepository;

@Service
public class ProductImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageService.class);

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("image/jpeg", "image/png", "image/webp");

    private static final long TAMANO_MAXIMO = 5L * 1024 * 1024;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ProductImageRepository piRepo;

    @Autowired
    private ProductoRepository pRepo;

    @Autowired
    private ProductImageMapper piMapper;


    // Guardar una imagen de un producto, subida a Cloudinary
    public ProductImage guardar(Long sku, MultipartFile file, boolean principal) {
        validarArchivo(file);

        Producto producto = obtenerProducto(sku);

        Map<?, ?> subida = subir(file, sku);

        String url = leerUrl(subida);
        String publicId = (String) subida.get("public_id");

        if (principal) {
            desmarcarPrincipales(sku);
        }

        ProductImage imagen = piMapper.toEntity(url, publicId, producto, principal);

        try {
            return piRepo.save(imagen);
        } catch (RuntimeException exception) {
            // Si la fila no se persiste, el asset queda huerfano en Cloudinary
            destruirEnCloudinary(publicId);
            throw exception;
        }
    }

    // Todas las imagenes de un producto, la principal primero.
    // Devuelve null si el producto no existe: el 404 lo decide el controller.
    public List<ProductImage> obtenerPorProducto(Long sku) {
        if (pRepo.findById(sku).isEmpty()) {
            return null;
        }

        return piRepo.findByProductoSkuOrderByPrincipalDescIdProductImageAsc(sku);
    }

    // Una imagen concreta de un producto. null si no existe.
    public ProductImage obtenerPorId(Long sku, Long idProductImage) {
        return piRepo.findByIdProductImageAndProductoSku(idProductImage, sku).orElse(null);
    }

    // Marcar una imagen como principal, desmarquando la que lo era
    public ProductImage editarPrincipal(Long sku, Long idProductImage) {
        ProductImage imagen = obtenerImagen(sku, idProductImage);

        desmarcarPrincipales(sku);
        imagen.setPrincipal(true);

        return piRepo.save(imagen);
    }

    // Borrar una imagen, en Cloudinary y en la base de datos
    public void borrar(Long sku, Long idProductImage) {
        ProductImage imagen = obtenerImagen(sku, idProductImage);

        // Si la destruccion falla la fila sobrevive para poder reintentar
        destruirEnCloudinary(imagen.getPublicId());

        piRepo.delete(imagen);
    }

    // Borrar todas las imagenes de un producto. Lo usa ProductoService.borrarEnCascada
    public void borrarTodas(Long sku) {
        List<ProductImage> imagenes = piRepo.findByProductoSkuOrderByPrincipalDescIdProductImageAsc(sku);

        for (ProductImage imagen : imagenes) {
            destruirEnCloudinary(imagen.getPublicId());
        }

        piRepo.deleteAll(imagenes);
    }

    // Cantidad de imagenes de un producto
    public long contarPorProducto(Long sku) {
        return piRepo.countByProductoSku(sku);
    }


    private Producto obtenerProducto(Long sku) {
        return pRepo.findById(sku)
                .orElseThrow(() -> new ProductoNoEncontradoException("No existe un producto con SKU " + sku));
    }

    private ProductImage obtenerImagen(Long sku, Long idProductImage) {
        ProductImage imagen = obtenerPorId(sku, idProductImage);

        if (imagen == null) {
            throw new ImagenNoEncontradaException(
                    "No existe una imagen con id " + idProductImage + " en el producto " + sku);
        }

        return imagen;
    }

    private void desmarcarPrincipales(Long sku) {
        List<ProductImage> principales = piRepo.findByProductoSkuAndPrincipalTrue(sku);

        for (ProductImage imagen : principales) {
            imagen.setPrincipal(false);
        }

        piRepo.saveAll(principales);
    }

    private Map<?, ?> subir(MultipartFile file, Long sku) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "productos/" + sku,
                            "resource_type", "image",
                            "allowed_formats", "jpg,jpg,png,webp"
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

    private void destruirEnCloudinary(String publicId) {
        Map<?, ?> resultado;

        try {
            resultado = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException | RuntimeException exception) {
            // Mismo criterio que en subir(): el mensaje del SDK puede traer el
            // api_key, así que al log solo va el tipo de excepción.
            LOGGER.error("Falló la eliminación del asset {} en Cloudinary ({})",
                    publicId, exception.getClass().getSimpleName());
            throw new ImagenInvalidaException("No se pudo eliminar la imagen de Cloudinary");
        }

        Object estado = resultado == null ? null : resultado.get("result");

        if (!"ok".equals(estado) && !"not found".equals(estado)) {
            LOGGER.error("Cloudinary devolvió {} al eliminar el asset {}", estado, publicId);
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
