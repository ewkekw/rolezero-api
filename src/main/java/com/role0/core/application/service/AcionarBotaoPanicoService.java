package com.role0.core.application.service;

import java.util.UUID;
import com.role0.core.application.port.out.ChatNotificationPort;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.usecase.AcionarBotaoPanicoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;

/**
 * ADR-001: Arquitetura de Comunicação do Botão de Pânico
 *
 * <p><strong>Contexto:</strong> Durante o andamento de um Rolê (Presencial), os usuários precisam reportar abusos ou riscos
 * de segurança imediatos. Uma vez reportado, é um dever perante a Política Zero-Knowledge "congelar" a exclusão temporária
 * dos logs do bate-papo usando a interface do sistema, para auditoria de terceiros competentes.</p>
 *
 * <p><strong>Decisão:</strong> A deleção das mensagens é impedida chamando a Outbound Port {@code ChatNotificationPort.congelarChatLegalmente}.
 * Isto mantém a inversão de dependência limpa sem misturar classes de Kafka/Rabbit no coração da aplicação.</p>
 *
 * <p><strong>Consequência (Positiva):</strong> Nível corporativo de isolamento de testes e cumprimento da legislação local
 * para resposta a incidentes sem ofuscar as demais lógicas da agregação.</p>
 */
import org.springframework.stereotype.Service;

@Service
public class AcionarBotaoPanicoService implements AcionarBotaoPanicoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final ChatNotificationPort chatNotificationPort;

    public AcionarBotaoPanicoService(EventoRepositoryPort eventoRepository, ChatNotificationPort chatNotificationPort) {
        this.eventoRepository = eventoRepository;
        this.chatNotificationPort = chatNotificationPort;
    }

    @Override
    public void executar(UUID eventoId, UUID reportadorId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento não encontrado."));

        if (!evento.getParticipantesAprovados().contains(reportadorId)) {
            throw new EventoDomainException("Apenas participantes aprovados no evento podem acionar a emergência.");
        }

        // Ativação da porta de contingência (Desvia do caminho feliz e aciona trava legal de logs temporários)
        chatNotificationPort.congelarChatLegalmente(evento.getId());
    }
}
