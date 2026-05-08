package com.role0.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.role0.adapter.out.persistence.entity.SolicitacaoParticipacaoJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataSolicitacaoRepository;
import com.role0.core.application.port.out.ListarSolicitacoesQueryPort;
import com.role0.core.domain.evento.valueobject.StatusSolicitacao;

@Component
public class ListarSolicitacoesQueryAdapter implements ListarSolicitacoesQueryPort {

    private final SpringDataSolicitacaoRepository repository;

    public ListarSolicitacoesQueryAdapter(SpringDataSolicitacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SolicitacaoParticipacaoJpaEntity> listarPorEventoEStatus(
            @NonNull UUID eventoId, @NonNull StatusSolicitacao status) {
        return repository.findByEventoIdAndStatus(eventoId, status);
    }

    @Override
    public List<SolicitacaoParticipacaoJpaEntity> listarAprovadosPorEvento(@NonNull UUID eventoId) {
        return repository.findByEventoIdAndStatus(eventoId, StatusSolicitacao.APROVADA);
    }
}
