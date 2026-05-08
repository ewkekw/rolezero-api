package com.role0.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_revogados")
public class TokenRevogadoJpaEntity {

    @Id
    @Column(name = "token_hash", length = 64, nullable = false)
    private String tokenHash;

    @Column(name = "data_revogacao", nullable = false)
    private LocalDateTime dataRevogacao;

    public TokenRevogadoJpaEntity() {
    }

    public TokenRevogadoJpaEntity(String tokenHash, LocalDateTime dataRevogacao) {
        this.tokenHash = tokenHash;
        this.dataRevogacao = dataRevogacao;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getDataRevogacao() {
        return dataRevogacao;
    }

    public void setDataRevogacao(LocalDateTime dataRevogacao) {
        this.dataRevogacao = dataRevogacao;
    }
}
