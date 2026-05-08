package com.role0.core.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.role0.adapter.in.web.dto.response.UsuarioPublicoResponse;
import com.role0.core.application.port.out.BuscarPerfilPublicoQueryPort;
import com.role0.core.domain.usuario.exception.UsuarioNaoEncontradoException;

@ExtendWith(MockitoExtension.class)
class BuscarPerfilPublicoServiceTest {

    @Mock
    private BuscarPerfilPublicoQueryPort queryPort;

    @InjectMocks
    private BuscarPerfilPublicoService service;

    @Test
    @SuppressWarnings("null")
    void deveRetornarPerfilPublico() {
        UUID userId = UUID.randomUUID();
        UsuarioPublicoResponse responseMock = new UsuarioPublicoResponse(
                userId, "Display Name", Collections.emptyList(), true, new BigDecimal("4.5"), 10);

        when(queryPort.buscarPerfilPublico(userId)).thenReturn(Optional.of(responseMock));

        UsuarioPublicoResponse result = service.executar(userId);

        assertEquals("Display Name", result.nomeDisplay());
        assertEquals(10, result.qtdAvaliacoes());
        verify(queryPort).buscarPerfilPublico(userId);
    }

    @Test
    @SuppressWarnings("null")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        UUID userId = UUID.randomUUID();
        when(queryPort.buscarPerfilPublico(userId)).thenReturn(Optional.empty());

        UsuarioNaoEncontradoException ex = assertThrows(UsuarioNaoEncontradoException.class, () -> service.executar(userId));
        org.junit.jupiter.api.Assertions.assertNotNull(ex);
        verify(queryPort).buscarPerfilPublico(userId);
    }
}
