package com.role0.core.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.role0.adapter.out.persistence.entity.SolicitacaoParticipacaoJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataSolicitacaoRepository;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.usecase.SolicitarParticipacaoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.valueobject.SolicitacaoParticipacao;
import com.role0.core.domain.evento.valueobject.StatusEvento;
import com.role0.core.domain.evento.valueobject.StatusSolicitacao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitarParticipacaoService implements SolicitarParticipacaoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final SpringDataSolicitacaoRepository solicitacaoRepository;

    public SolicitarParticipacaoService(EventoRepositoryPort eventoRepository,
            SpringDataSolicitacaoRepository solicitacaoRepository) {
        this.eventoRepository = eventoRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @Override
    @Transactional
    public SolicitacaoParticipacao executar(UUID eventoId, UUID participanteId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento não localizado"));

        if (evento.getStatus() != StatusEvento.ABERTO_PARA_VAGAS) {
            throw new EventoDomainException("Evento já fechado ou não aceita vagas no momento.");
        }

        SolicitacaoParticipacaoJpaEntity entity = new SolicitacaoParticipacaoJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setEventoId(eventoId);
        entity.setUsuarioId(participanteId);
        entity.setStatus(StatusSolicitacao.PENDENTE);
        entity.setDataSolicitacao(LocalDateTime.now());
        solicitacaoRepository.save(entity);

        return new SolicitacaoParticipacao(entity.getId(), participanteId, eventoId);
    }
}
