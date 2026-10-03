package com.esam.esam_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.esam.esam_backend.dto.direccion.DireccionDTORequest;
import com.esam.esam_backend.mapper.DireccionMapper;
import com.esam.esam_backend.model.Comuna;
import com.esam.esam_backend.model.Direccion;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.repository.ComunaRepository;
import com.esam.esam_backend.repository.DireccionRepository;
import com.esam.esam_backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class DireccionServiceTests {

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private DireccionMapper direccionMapper;

    @InjectMocks
    private DireccionService direccionService;

    @Test
    void crearAsignaElUsuarioAutenticadoAunqueElRequestIndiqueOtro() {
        Usuario usuarioAutenticado = usuario(7L);
        Comuna comuna = new Comuna();
        DireccionDTORequest request = new DireccionDTORequest();
        request.setIdUsuario(99L);
        request.setIdComuna(3L);
        Direccion direccion = new Direccion();

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuarioAutenticado));
        when(comunaRepository.findById(3L)).thenReturn(Optional.of(comuna));
        when(direccionMapper.toEntity(request, usuarioAutenticado, comuna)).thenReturn(direccion);
        when(direccionRepository.save(direccion)).thenReturn(direccion);

        direccionService.guardar(request, 7L);

        verify(usuarioRepository).findById(7L);
        verify(usuarioRepository, never()).findById(99L);
        verify(direccionRepository).save(direccion);
    }

    @Test
    void noPermiteLeerDireccionDeOtroUsuario() {
        Direccion direccion = direccion(15L, 99L);
        when(direccionRepository.findById(15L)).thenReturn(Optional.of(direccion));

        assertThrows(AccessDeniedException.class, () -> direccionService.obtenerPorId(15L, 7L));
    }

    @Test
    void noPermiteEditarDireccionDeOtroUsuarioNiTransferirla() {
        Direccion direccion = direccion(15L, 99L);
        DireccionDTORequest request = new DireccionDTORequest();
        request.setIdUsuario(7L);
        when(direccionRepository.findById(15L)).thenReturn(Optional.of(direccion));

        assertThrows(AccessDeniedException.class, () -> direccionService.editar(15L, request, 7L));

        verify(direccionRepository, never()).save(any(Direccion.class));
    }

    @Test
    void noPermiteEliminarDireccionDeOtroUsuario() {
        Direccion direccion = direccion(15L, 99L);
        when(direccionRepository.findById(15L)).thenReturn(Optional.of(direccion));

        assertThrows(AccessDeniedException.class, () -> direccionService.borrar(15L, 7L));

        verify(direccionRepository, never()).delete(any(Direccion.class));
    }

    @Test
    void modificarDireccionPropiaConservaLaPropiedadAunqueElRequestIndiqueOtroUsuario() {
        Usuario usuarioAutenticado = usuario(7L);
        Direccion direccion = direccion(15L, 7L);
        Comuna comuna = new Comuna();
        DireccionDTORequest request = new DireccionDTORequest();
        request.setIdUsuario(99L);
        request.setIdComuna(3L);
        request.setPredeterminada(false);

        when(direccionRepository.findById(15L)).thenReturn(Optional.of(direccion));
        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(usuarioAutenticado));
        when(comunaRepository.findById(3L)).thenReturn(Optional.of(comuna));
        when(direccionRepository.save(any(Direccion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Direccion resultado = direccionService.editar(15L, request, 7L);

        assertEquals(7L, resultado.getUsuario().getIdUsuario());
        ArgumentCaptor<Direccion> captor = ArgumentCaptor.forClass(Direccion.class);
        verify(direccionRepository).save(captor.capture());
        assertEquals(7L, captor.getValue().getUsuario().getIdUsuario());
        verify(usuarioRepository, never()).findById(99L);
    }

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        return usuario;
    }

    private Direccion direccion(Long id, Long idUsuario) {
        Direccion direccion = new Direccion();
        direccion.setIdDireccion(id);
        direccion.setUsuario(usuario(idUsuario));
        direccion.setActivo(true);
        direccion.setPredeterminada(false);
        return direccion;
    }
}
