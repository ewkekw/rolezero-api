package com.role0.adapter.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role_mensagem_chat")
public class MensagemChatJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "conteudo", nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "timestamp_envio", nullable = false)
    private LocalDateTime timestampEnvio;

    @Column(name = "tipo")
    private String tipo; // TEXT, SYSTEM, etc.

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEventoId() { return eventoId; }
    public void setEventoId(UUID eventoId) { this.eventoId = eventoId; }
    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public LocalDateTime getTimestampEnvio() { return timestampEnvio; }
    public void setTimestampEnvio(LocalDateTime timestampEnvio) { this.timestampEnvio = timestampEnvio; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
