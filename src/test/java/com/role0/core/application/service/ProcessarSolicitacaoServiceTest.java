package com.role0.core.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.role0.core.application.port.out.ChatNotificationPort;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.MessageBrokerEventPort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.service.GatilhoSocialService;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.evento.valueobject.StatusEvento;

@ExtendWith(MockitoExtension.class)
class ProcessarSolicitacaoServiceTest {

    @Mock
    private EventoRepositoryPort eventoRepository;

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private ChatNotificationPort chatNotification;

    @Mock
    private MessageBrokerEventPort messageBroker;

    @Mock
    private GatilhoSocialService gatilhoSocialService;

    @InjectMocks
    private ProcessarSolicitacaoService processarSolicitacaoService;

    private UUID eventoId;
    private UUID hostId;
    private UUID participanteId;
    private Evento eventoMock;

    @BeforeEach
    void setUp() {

        eventoId = UUID.randomUUID();
        hostId = UUID.randomUUID();
        participanteId = UUID.randomUUID();
        eventoMock = new Evento(
            eventoId, 
            hostId,
            "Festa Teste", 
            2,
            new CoordenadaGeografica(0.0, 0.0), 
            LocalDateTime.now().plusHours(3)
        );
        eventoMock.setStatus(StatusEvento.ABERTO_PARA_VAGAS);
    }

    @Test
    @DisplayName("Host deve poder aprovar participante com sucesso")
    void deveAprovarParticipante() {
        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(eventoMock));

        processarSolicitacaoService.aprovar(eventoId, hostId, participanteId);

        verify(eventoRepository).salvar(eventoMock);
        verify(chatNotification).notificarNovoParticipante(eventoId, participanteId);
        // O evento precisa de 2 vagas. Aprovar só 1 não fecha o grupo.
        verify(messageBroker, never()).agendarEncerramentoDeEvento(any(), anyLong());
    }

    @Test
    @DisplayName("Apenas o host (Anfitrião) pode aprovar pessoas")
    void naoDeveAprovarParticipanteSeNaoForHost() {
        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(eventoMock));

        UUID intrusoId = UUID.randomUUID();

        assertThrows(EventoDomainException.class, () -> {
            processarSolicitacaoService.aprovar(eventoId, intrusoId, participanteId);
        });

        verify(eventoRepository, never()).salvar(any());
        verify(chatNotification, never()).notificarNovoParticipante(any(), any());
    }

    @Test
    @DisplayName("Deve fechar o grupo e disparar Icebreaker ao esgotar as vagas")
    void deveEsgotarVagasEFecharGrupo() {
        // Força o evento a ja ter 1 vaga consumida para a próxima solicitação esgotar.
        eventoMock.aprovarParticipante(participanteId); 
        
        UUID participante2 = UUID.randomUUID();

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(eventoMock));

        processarSolicitacaoService.aprovar(eventoId, hostId, participante2);

        verify(eventoRepository).salvar(eventoMock);
        // Grupo agora está fechado. Verifica gatilho social:
        verify(gatilhoSocialService).gerarIceBreaker(eq(eventoMock), any());
        verify(messageBroker).agendarEncerramentoDeEvento(eventoId, 24);
    }
}
