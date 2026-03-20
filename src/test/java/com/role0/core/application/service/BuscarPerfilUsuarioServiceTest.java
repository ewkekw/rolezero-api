package com.role0.core.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.role0.adapter.in.web.dto.response.UsuarioPerfilResponse;
import com.role0.core.application.port.out.BuscarPerfilUsuarioQueryPort;
import com.role0.core.domain.usuario.exception.UsuarioDomainException;

@ExtendWith(MockitoExtension.class)
class BuscarPerfilUsuarioServiceTest {

    @Mock
    private BuscarPerfilUsuarioQueryPort queryPort;

    @InjectMocks
    private BuscarPerfilUsuarioService buscarPerfilUsuarioService;

    @Test
    void deveRetornarPerfilComSucessoSeExistir() {
        UUID id = UUID.randomUUID();
        UsuarioPerfilResponse mockRep = new UsuarioPerfilResponse(
                id, "João", "joao@email.com", List.of("TRIP", "TECH"), true, new BigDecimal("4.85")
        );

        when(queryPort.buscarPerfil(id)).thenReturn(Optional.of(mockRep));

        UsuarioPerfilResponse res = buscarPerfilUsuarioService.executar(id);

        assertEquals("joao@email.com", res.email());
        assertEquals("João", res.nome());
        assertEquals(new BigDecimal("4.85"), res.trustScore());
    }

    @Test
    void deveEstourarExcecaoSeUsuarioNaoExistir() {
        UUID id = UUID.randomUUID();
        when(queryPort.buscarPerfil(id)).thenReturn(Optional.empty());

        UsuarioDomainException ex = assertThrows(UsuarioDomainException.class, () -> {
            buscarPerfilUsuarioService.executar(id);
        });
        org.junit.jupiter.api.Assertions.assertNotNull(ex);
    }
}
