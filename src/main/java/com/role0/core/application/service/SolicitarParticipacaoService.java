package com.role0.core.application.service;

import java.util.UUID;

import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.usecase.SolicitarParticipacaoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.valueobject.SolicitacaoParticipacao;
import com.role0.core.domain.evento.valueobject.StatusEvento;

import org.springframework.stereotype.Service;

@Service
public class SolicitarParticipacaoService implements SolicitarParticipacaoUseCase {

    private final EventoRepositoryPort eventoRepository;

    public SolicitarParticipacaoService(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public SolicitacaoParticipacao executar(UUID eventoId, UUID participanteId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento não localizado"));

        if (evento.getStatus() != StatusEvento.ABERTO_PARA_VAGAS) {
            throw new EventoDomainException("Evento já fechado ou não aceita vagas no momento.");
        }
        
        // Retorna a intenção, que será salva na tabela pivot posteriormente
        return new SolicitacaoParticipacao(UUID.randomUUID(), participanteId, eventoId);
    }
}
