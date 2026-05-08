package com.role0.adapter.in.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.role0.adapter.in.web.dto.response.UsuarioPerfilResponse;
import com.role0.adapter.in.web.dto.response.UsuarioPublicoResponse;
import com.role0.core.application.usecase.BuscarPerfilPublicoUseCase;
import com.role0.core.application.usecase.BuscarPerfilUsuarioUseCase;
import com.role0.core.domain.usuario.exception.UsuarioNaoEncontradoException;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioQueryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuscarPerfilUsuarioUseCase buscarPerfilUsuarioUseCase;

    @MockitoBean
    private BuscarPerfilPublicoUseCase buscarPerfilPublicoUseCase;

    @Test
    @WithMockUser(username = "3fa85f64-5717-4562-b3fc-2c963f66afa6", roles = "USER")
    @SuppressWarnings("null")
    void deveBuscarMeuPerfilCorretamenteUsandoJwtClaimMockada() throws Exception {
        UUID expectedId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

        UsuarioPerfilResponse mockResp = new UsuarioPerfilResponse(
                expectedId, "Fulano", "fulano@role.com", List.of("CHILL"), false, new BigDecimal("3.5"));

        when(buscarPerfilUsuarioUseCase.executar(expectedId)).thenReturn(mockResp);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Fulano"))
                .andExpect(jsonPath("$.email").value("fulano@role.com"))
                .andExpect(jsonPath("$.trustScore").value(3.5));
    }

    @Test
    void deveProtegerRotaSemAuth() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized()); // ou Forbidden dependendo da config
    }

    @Test
    @SuppressWarnings("null")
    void deveBuscarPerfilPublicoCorretamente() throws Exception {
        UUID queryId = UUID.randomUUID();

        UsuarioPublicoResponse mockResp = new UsuarioPublicoResponse(
                queryId, "Ciclano", List.of("ENERGICO"), true, new BigDecimal("4.8"), 15);

        when(buscarPerfilPublicoUseCase.executar(queryId)).thenReturn(mockResp);

        mockMvc.perform(get("/api/v1/users/" + queryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDisplay").value("Ciclano"))
                .andExpect(jsonPath("$.trustScore").value(4.8))
                .andExpect(jsonPath("$.qtdAvaliacoes").value(15))
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    @SuppressWarnings("null")
    void deveRetornar404QuandoPerfilPublicoNaoEncontrado() throws Exception {
        UUID queryId = UUID.randomUUID();

        when(buscarPerfilPublicoUseCase.executar(queryId)).thenThrow(new UsuarioNaoEncontradoException("Não encontrado"));

        mockMvc.perform(get("/api/v1/users/" + queryId))
                .andExpect(status().isNotFound()); // Depende do GlobalExceptionHandler retornar 404 para essa exceção
    }
}
