package com.esam.esam_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import com.esam.esam_backend.dto.direccion.DireccionDTORequest;
import com.esam.esam_backend.exception.ComunaInvalidaException;
import com.esam.esam_backend.exception.DireccionInvalidaException;
import com.esam.esam_backend.exception.DireccionNoEncontradaException;
import com.esam.esam_backend.exception.UsuarioInvalidaException;
import com.esam.esam_backend.mapper.DireccionMapper;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.DireccionRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository dRepo;
    private final UsuarioRepository uRepo;
    private final ComunaRepository cRepo;
    private final DireccionMapper dMapper;

    public List<Direccion> obtenerPorUsuario(Long idUsuario) {
        return dRepo.findByUsuarioIdUsuario(idUsuario);
    }

    public List<Direccion> obtenerActivasPorUsuario(Long idUsuario) {
        return dRepo.findByUsuarioIdUsuarioAndActivoTrueOrderByIdDireccionAsc(idUsuario);
    }

    public Direccion obtenerPorId(Long id, Long idUsuarioAutenticado) {
        Direccion direccion = dRepo.findById(id).orElse(null);
        if (direccion != null) {
            validarPropietario(direccion.getUsuario().getIdUsuario(), idUsuarioAutenticado);
        }
        return direccion;
    }

    public Direccion obtenerPorIdRequerida(Long id) {
        return obtenerDireccion(id);
    }

    public void validarPropietario(Long idUsuarioSolicitado, Long idUsuarioAutenticado) {
        if (!idUsuarioAutenticado.equals(idUsuarioSolicitado)) {
            throw new AccessDeniedException("No tiene permiso para acceder a esta dirección");
        }
    }

    @Transactional
    public Direccion guardar(DireccionDTORequest request, Long idUsuarioAutenticado) {
        Usuario usuario = obtenerUsuario(idUsuarioAutenticado);
        Comuna comuna = obtenerComuna(request.getIdComuna());

        Direccion direccion = dMapper.toEntity(request, usuario, comuna);
        if (Boolean.TRUE.equals(direccion.getPredeterminada())) {
            quitarPredeterminadaDeOtras(usuario.getIdUsuario(), null);
        }

        return dRepo.save(direccion);
    }

    @Transactional
    public Direccion editar(Long id, DireccionDTORequest request, Long idUsuarioAutenticado) {
        Direccion direccion = obtenerDireccion(id);
        validarPropietario(direccion.getUsuario().getIdUsuario(), idUsuarioAutenticado);
        Usuario usuario = obtenerUsuario(idUsuarioAutenticado);
        Comuna comuna = obtenerComuna(request.getIdComuna());

        if (Boolean.TRUE.equals(request.getPredeterminada()) && !Boolean.TRUE.equals(direccion.getActivo())) {
            throw new DireccionInvalidaException("Una dirección inactiva no puede ser predeterminada");
        }

        direccion.setNombreReceptor(request.getNombreReceptor());
        direccion.setTelefonoReceptor(request.getTelefonoReceptor());
        direccion.setCalle(request.getCalle());
        direccion.setNumero(request.getNumero());
        direccion.setComplemento(request.getComplemento());
        direccion.setPredeterminada(Boolean.TRUE.equals(request.getPredeterminada()));
        direccion.setUsuario(usuario);
        direccion.setComuna(comuna);

        if (Boolean.TRUE.equals(direccion.getPredeterminada())) {
            quitarPredeterminadaDeOtras(usuario.getIdUsuario(), id);
        }

        return dRepo.save(direccion);
    }

    @Transactional
    public void borrar(Long id, Long idUsuarioAutenticado) {
        Direccion direccion = obtenerDireccion(id);
        validarPropietario(direccion.getUsuario().getIdUsuario(), idUsuarioAutenticado);
        Usuario usuario = direccion.getUsuario();

        dRepo.delete(direccion);

        if (usuario != null && Boolean.TRUE.equals(direccion.getPredeterminada())) {
            List<Direccion> restantes = dRepo.findByUsuarioIdUsuarioAndActivoTrueOrderByIdDireccionAsc(
                    usuario.getIdUsuario());
            if (!restantes.isEmpty()) {
                Direccion nuevaPredeterminada = restantes.get(0);
                quitarPredeterminadaDeOtras(usuario.getIdUsuario(), nuevaPredeterminada.getIdDireccion());
                nuevaPredeterminada.setPredeterminada(true);
                dRepo.save(nuevaPredeterminada);
            }
        }
    }

    private void quitarPredeterminadaDeOtras(Long idUsuario, Long idDireccionExcluida) {
        for (Direccion otra : dRepo.findByUsuarioIdUsuarioAndActivoTrueOrderByIdDireccionAsc(idUsuario)) {
            if (idDireccionExcluida == null || !otra.getIdDireccion().equals(idDireccionExcluida)) {
                otra.setPredeterminada(false);
                dRepo.save(otra);
            }
        }
    }

    private Usuario obtenerUsuario(Long idUsuario) {
        return uRepo.findById(idUsuario)
                .orElseThrow(() -> new UsuarioInvalidaException("No existe un usuario con id " + idUsuario));
    }

    private Comuna obtenerComuna(Long idComuna) {
        return cRepo.findById(idComuna)
                .orElseThrow(() -> new ComunaInvalidaException("No existe una comuna con id " + idComuna));
    }

    private Direccion obtenerDireccion(Long id) {
        return dRepo.findById(id)
                .orElseThrow(() -> new DireccionNoEncontradaException("No existe una dirección con id " + id));
    }
}
