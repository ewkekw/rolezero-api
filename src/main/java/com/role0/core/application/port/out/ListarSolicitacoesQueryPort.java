package com.role0.core.application.port.out;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import com.role0.adapter.out.persistence.entity.SolicitacaoParticipacaoJpaEntity;
import com.role0.core.domain.evento.valueobject.StatusSolicitacao;

public interface ListarSolicitacoesQueryPort {
    List<SolicitacaoParticipacaoJpaEntity> listarPorEventoEStatus(
            @NonNull UUID eventoId, @NonNull StatusSolicitacao status);

    List<SolicitacaoParticipacaoJpaEntity> listarAprovadosPorEvento(@NonNull UUID eventoId);
}
