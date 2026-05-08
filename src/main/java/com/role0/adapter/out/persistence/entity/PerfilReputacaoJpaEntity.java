package com.role0.adapter.out.persistence.entity;

import java.util.UUID;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role_perfil_reputacao")
public class PerfilReputacaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private UUID usuarioId;

    @Column(name = "trust_score", nullable = false)
    private BigDecimal currentScore;

    @Column(name = "avaliacoes_totais", nullable = false)
    private int totalAvaliacoes;

    // Default constructor for JPA
    protected PerfilReputacaoJpaEntity() {
    }

    public PerfilReputacaoJpaEntity(UUID usuarioId, BigDecimal currentScore, int totalAvaliacoes) {
        this.usuarioId = usuarioId;
        this.currentScore = currentScore;
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public BigDecimal getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(BigDecimal currentScore) {
        this.currentScore = currentScore;
    }

    public int getTotalAvaliacoes() {
        return totalAvaliacoes;
    }

    public void setTotalAvaliacoes(int totalAvaliacoes) {
        this.totalAvaliacoes = totalAvaliacoes;
    }
}
