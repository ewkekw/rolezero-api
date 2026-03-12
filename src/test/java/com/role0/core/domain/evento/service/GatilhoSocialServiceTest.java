package com.role0.core.domain.evento.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.usuario.entity.Usuario;
import com.role0.core.domain.usuario.valueobject.VibeTag;

class GatilhoSocialServiceTest {

    private GatilhoSocialService gatilhoSocialService;
    private Evento eventoMock;

    @BeforeEach
    void setUp() {
        gatilhoSocialService = new GatilhoSocialService();
        eventoMock = new Evento(
            UUID.randomUUID(), 
            UUID.randomUUID(),
            "Festa Mock", 
            10,
            new CoordenadaGeografica(0.0, 0.0),
            LocalDateTime.now().plusHours(2)
        );
    }

    @Test
    @DisplayName("Deve retornar fallback se houver menos de 2 participantes aprovados")
    void deveRetornarMensagemPadrao_QuandoPoucosParticipantes() {
        String icebreaker = gatilhoSocialService.gerarIceBreaker(eventoMock, List.of(mockUsuario(Set.of(VibeTag.TECH_TALKS))));
        assertEquals("Bem-vindos ao rolê!", icebreaker);
    }

    @Test
    @DisplayName("Deve identificar a tag comum de Tecnologia entre os usuários")
    void deveRetornarTagTechEmComum() {
        Usuario u1 = mockUsuario(Set.of(VibeTag.TECH_TALKS, VibeTag.BOARD_GAMES));
        Usuario u2 = mockUsuario(Set.of(VibeTag.TECH_TALKS, VibeTag.INDIE_MUSIC));
        Usuario u3 = mockUsuario(Set.of(VibeTag.TECH_TALKS, VibeTag.CAFE));

        String icebreaker = gatilhoSocialService.gerarIceBreaker(eventoMock, List.of(u1, u2, u3));
        assertTrue(icebreaker.contains("TECH_TALKS"));
    }

    @Test
    @DisplayName("Deve retornar mensagem padrao de grupo fechado quando não tem Tags em comum")
    void deveRetornarGrupoFechadoQuandoZeroTags() {
        Usuario u1 = mockUsuario(Set.of(VibeTag.BOARD_GAMES));
        Usuario u2 = mockUsuario(Set.of(VibeTag.INDIE_MUSIC));

        String icebreaker = gatilhoSocialService.gerarIceBreaker(eventoMock, List.of(u1, u2));
        assertEquals("O grupo fechou! Prontos para o rolê?", icebreaker);
    }

    private Usuario mockUsuario(Set<VibeTag> tags) {
        Usuario u = new Usuario(UUID.randomUUID(), "User Test");
        for (VibeTag t : tags) {
            u.adicionarVibeTag(t);
        }
        return u;
    }
}
