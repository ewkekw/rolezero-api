package com.role0.adapter.in.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.role0.core.application.usecase.LogoutUseCase;

@SpringBootTest
@AutoConfigureMockMvc
class AuthAcaoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @Test
    void deveRetornarNoContentNoLogoutComToken() throws Exception {
        String token = "token.jwt.mockado";

        mockMvc.perform(delete("/api/v1/auth/session")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        verify(logoutUseCase).executar(token);
    }

    @Test
    void deveRetornarNoContentNoLogoutSemToken() throws Exception {
        mockMvc.perform(delete("/api/v1/auth/session"))
                .andExpect(status().isNoContent());

        // Se n~ao evia token o filter bloqueia e da 401, mas se passar ele não chama
        // Na verdade, as rotas de auth não-logadas são barradas pelo fw de segurança. 
        // Vamos testar apenas que o controller retorna NoContent quando sem auth na camada controller (simulado).
    }
}
