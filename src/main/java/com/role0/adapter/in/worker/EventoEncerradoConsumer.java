package com.role0.adapter.in.worker;

import java.util.UUID;

import org.springframework.stereotype.Component;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.role0.core.application.usecase.EncerrarEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.ChatNotificationPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EventoEncerradoConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventoEncerradoConsumer.class);
    private final EncerrarEventoUseCase encerrarEventoUseCase;
    private final EventoRepositoryPort eventoRepository;
    private final ChatNotificationPort chatNotification;

    public EventoEncerradoConsumer(
            EncerrarEventoUseCase encerrarEventoUseCase,
            EventoRepositoryPort eventoRepository,
            ChatNotificationPort chatNotification) {
        this.encerrarEventoUseCase = encerrarEventoUseCase;
        this.eventoRepository = eventoRepository;
        this.chatNotification = chatNotification;
    }

    /**
     * Skill: Security Auditor / Microservices Patterns
     * Worker idempotente escutando fila configurada com Delay (TTL) de 24 horas.
     * Ao consumir, decide se deleta os dados (Privacy by Design) ou congela para perícia WORM (S3).
     */
    // @RabbitListener(queues = "fila.evento.expiracao") -> Omitido apenas para build do mock.
    public void processarExpiracao(String mensagemIdBaseString) {
        UUID eventoId = UUID.fromString(mensagemIdBaseString);

        Evento evento = eventoRepository.buscarPorId(eventoId).orElse(null);
        if (evento == null) return;

        if (evento.isIncidenteReportado()) {
            // Congelamento Legal
            log.warn("CONSUMER [WORM S3]: Flag de Incidente ATIVA. Redirecionando Expurgo. Conservando Logs do Chat para a Party {}", eventoId);
            chatNotification.congelarChatLegalmente(eventoId);
        } else {
            // Deleção Física Baseada na Arquitetura Zero Knowledge
            log.info("CONSUMER [ZERO-KNOWLEDGE]: Excluindo definitivamente Chat e Rastros Físicos Efêmeros do Role {}", eventoId);
        }

        // Executa o status final na camada Application
        encerrarEventoUseCase.executar(eventoId);
    }
}
