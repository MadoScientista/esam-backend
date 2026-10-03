package com.esam.esam_backend.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.esam.esam_backend.dto.direccion.DireccionDTORequest;
import com.esam.esam_backend.mapper.DireccionMapper;
import com.esam.esam_backend.model.Usuario;
import com.esam.esam_backend.security.CustomUserDetails;
import com.esam.esam_backend.service.DireccionService;

@ExtendWith(MockitoExtension.class)
class DireccionControllerTests {

    @Mock
    private DireccionService direccionService;

    @Mock
    private DireccionMapper direccionMapper;

    @InjectMocks
    private DireccionController direccionController;

    @Test
    void listarSinFiltroUsaLaIdentidadAutenticada() {
        direccionController.obtenerTodos(principal(7L));

        verify(direccionService).obtenerPorUsuario(7L);
    }

    @Test
    void rutaDeUsuarioAjenoSeRechaza() {
        doThrow(new AccessDeniedException("denegado"))
                .when(direccionService).validarPropietario(99L, 7L);

        assertThrows(AccessDeniedException.class,
                () -> direccionController.obtenerPorUsuario(99L, principal(7L)));
    }

    @Test
    void crearUsaElUsuarioAutenticadoEnLugarDelIdDelRequest() {
        DireccionDTORequest request = new DireccionDTORequest();
        request.setIdUsuario(99L);

        direccionController.guardar(request, principal(7L));

        verify(direccionService).guardar(request, 7L);
    }

    @Test
    void operacionesPorIdPasanElIdDelUsuarioAutenticado() {
        DireccionDTORequest request = new DireccionDTORequest();
        CustomUserDetails principal = principal(7L);

        direccionController.obtenerPorId(15L, principal);
        direccionController.editar(15L, request, principal);
        direccionController.borrar(15L, principal);

        verify(direccionService).obtenerPorId(15L, 7L);
        verify(direccionService).editar(15L, request, 7L);
        verify(direccionService).borrar(15L, 7L);
    }

    private CustomUserDetails principal(Long id) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        return new CustomUserDetails(usuario);
    }
}
