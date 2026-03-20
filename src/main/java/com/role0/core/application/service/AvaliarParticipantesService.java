package com.role0.core.application.service;

import java.util.UUID;

import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.usecase.AvaliarParticipantesUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.valueobject.StatusEvento;

import org.springframework.stereotype.Service;

@Service
public class AvaliarParticipantesService implements AvaliarParticipantesUseCase {

    private final EventoRepositoryPort eventoRepository;
    // Num fluxo real necessitará chamar o bounded context de reputação, ex: ReputacaoServicePort

    public AvaliarParticipantesService(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public void executar(UUID eventoId, UUID avaliadorId, UUID avaliadoId, double nota) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento não localizado."));
            
        if (evento.getStatus() != StatusEvento.EXPIRADO && evento.getStatus() != StatusEvento.EM_ANDAMENTO) {
            throw new EventoDomainException("Você só pode avaliar a comunidade após ou durante o acontecimento do evento.");
        }
        
        // Regra de segurança relacional: Ambos precisavam estar confirmados na mesma party
        if (!evento.getParticipantesAprovados().contains(avaliadorId) || !evento.getParticipantesAprovados().contains(avaliadoId)) {
            throw new EventoDomainException("Avaliações no Role-Zero apenas são permitidas entre frequentadores confirmados.");
        }
        
        // Chamada outabound de pontuação não implementada nesta casca.
    }
}
