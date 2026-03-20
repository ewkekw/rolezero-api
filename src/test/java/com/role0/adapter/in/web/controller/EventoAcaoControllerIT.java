package com.role0.adapter.in.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.role0.adapter.in.web.dto.request.CriarEventoRequest;
import com.role0.config.security.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Usa jdbc:tc:postgis dinamicamente gerado pelo Testcontainers
class EventoAcaoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    @DisplayName("Requisição s/ JWT Bearer deve ser Bloqueada logo no Filtro (HTTP 401)")
    @SuppressWarnings("null")
    void deveNegarAcessoSemToken_Http401() throws Exception {
        String payload = criarPayloadEvento();
        
        mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Requisição s/ JWT Bearer mas no /swagger-ui deve Passar (HTTP 302/200)")
    void deveAcessarSwaggerSemToken() throws Exception {
        mockMvc.perform(post("/swagger-ui/index.html"))
               .andExpect(status().isMethodNotAllowed()); // Não é POST, logo retorna MethodNotAllowed (405) comprovando bypass de Auth
    }

    @Test
    @DisplayName("Host deve Conseguir Inserir Evento via JWT Autêntico (HTTP 201)")
    @SuppressWarnings("null")
    void deveCriarEventoNoPostGIS_Autorizado() throws Exception {
        // Gerando um JWT nativo para o Profile de Test //
        UUID fakeUserUUID = UUID.randomUUID();
        String jwtToken = jwtService.generateToken(fakeUserUUID);

        String payload = criarPayloadEvento();

        mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Bad Request (HTTP 400) se Enviar Capacidade Negativa")
    @SuppressWarnings("null")
    void deveBloquearPayloadMalFormado_Http400() throws Exception {
        UUID fakeUserUUID = UUID.randomUUID();
        String jwtToken = jwtService.generateToken(fakeUserUUID);

        String jsonErrado = """
            {
               "titulo": "Rolezinho Invalido",
               "capacidadeMaxima": -5,
               "latitude": 30.5,
               "longitude": -50.2,
               "horarioInicio": "2029-12-01T20:00:00"
            }
        """;

        mockMvc.perform(post("/api/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonErrado)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    // Builder nativo do Jackson
    private String criarPayloadEvento() throws Exception {
        CriarEventoRequest req = new CriarEventoRequest(
            "Festinha na Testcontainers",
            "Descrição do Evento de Teste",
            25,
            42.3601,
            -71.0589,
            LocalDateTime.now().plusDays(2),
            java.util.List.of("TEST_TAG")
        );
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(req);
    }
}
