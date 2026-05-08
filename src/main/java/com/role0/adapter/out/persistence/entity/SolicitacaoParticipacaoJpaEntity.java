package com.role0.adapter.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.role0.core.domain.evento.valueobject.StatusSolicitacao;

@Entity
@Table(name = "role_solicitacao_participacao")
public class SolicitacaoParticipacaoJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_solicitacao", nullable = false)
    private StatusSolicitacao status;

    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEventoId() { return eventoId; }
    public void setEventoId(UUID eventoId) { this.eventoId = eventoId; }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) { this.status = status; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }
}
