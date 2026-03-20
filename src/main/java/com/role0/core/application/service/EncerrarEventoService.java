package com.role0.core.application.service;

import java.util.UUID;

import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.usecase.EncerrarEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.valueobject.StatusEvento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ADR-002: Fluxo EDA (Event-Driven Architecture) para Expurgo de Informação
 *
 * <p><strong>Contexto:</strong> Uma regra absoluta do Role0 era apagar contatos (chat) com exatidão após
 * o término da sessão (Privacy by Design / Zero-Knowledge message retention).</p>
 * 
 * <p><strong>Decisão:</strong> Subimos isso por uma fila Delayed Exchange gerada via MessageBrokerPort no momento do Match, 
 * cuja exaustão (consumo final) ativa nativamente este Use Case "EncerrarEvento".</p>
 */
import org.springframework.stereotype.Service;

@Service
public class EncerrarEventoService implements EncerrarEventoUseCase {

    private static final Logger log = LoggerFactory.getLogger(EncerrarEventoService.class);
    private final EventoRepositoryPort eventoRepository;

    public EncerrarEventoService(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public void executar(UUID eventoId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento não encontrado para expiração."));

        evento.setStatus(StatusEvento.EXPIRADO);
        eventoRepository.salvar(evento);
        
        log.info("Ocorrência finalizada via EDA. Dados deletáveis de chat poderão ser fisicamente espurgados pelo consumer.");
    }
}
