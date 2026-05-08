package com.role0.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.role0.adapter.out.persistence.entity.SolicitacaoParticipacaoJpaEntity;
import com.role0.core.domain.evento.valueobject.StatusSolicitacao;

@Repository
public interface SpringDataSolicitacaoRepository
        extends JpaRepository<SolicitacaoParticipacaoJpaEntity, UUID> {

    List<SolicitacaoParticipacaoJpaEntity> findByEventoIdAndStatus(
            UUID eventoId, StatusSolicitacao status);

    List<SolicitacaoParticipacaoJpaEntity> findByEventoId(UUID eventoId);
}
