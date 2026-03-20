package com.role0.adapter.in.web.controller;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.role0.adapter.in.web.dto.request.AtualizarPerfilRequest;
import com.role0.core.application.usecase.AtualizarPerfilUseCase;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioAcaoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AtualizarPerfilUseCase atualizarPerfilUseCase;

    @Test
    @WithMockUser(username = "3fa85f64-5717-4562-b3fc-2c963f66afa6", roles = "USER")
    @SuppressWarnings("null")
    void deveAtualizarPerfilEGanhar204() throws Exception {
        UUID expectedId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        AtualizarPerfilRequest req = new AtualizarPerfilRequest("Novo Display Name");

        doNothing().when(atualizarPerfilUseCase).executar(expectedId, "Novo Display Name");

        mockMvc.perform(patch("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "3fa85f64-5717-4562-b3fc-2c963f66afa6", roles = "USER")
    @SuppressWarnings("null")
    void deveRecusarQuandoNomeVazio() throws Exception {
        AtualizarPerfilRequest req = new AtualizarPerfilRequest("  ");

        mockMvc.perform(patch("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
