package com.role0.adapter.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role_avaliacao")
public class AvaliacaoJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "avaliador_id", nullable = false)
    private UUID avaliadorId;

    @Column(name = "avaliado_id", nullable = false)
    private UUID avaliadoId;

    @Column(name = "evento_id")
    private UUID eventoId;

    @Column(name = "nota", nullable = false)
    private int nota;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AvaliacaoJpaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAvaliadorId() { return avaliadorId; }
    public void setAvaliadorId(UUID avaliadorId) { this.avaliadorId = avaliadorId; }
    public UUID getAvaliadoId() { return avaliadoId; }
    public void setAvaliadoId(UUID avaliadoId) { this.avaliadoId = avaliadoId; }
    public UUID getEventoId() { return eventoId; }
    public void setEventoId(UUID eventoId) { this.eventoId = eventoId; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
