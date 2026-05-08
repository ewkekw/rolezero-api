package com.role0.core.domain.evento.valueobject;

import java.util.UUID;

public class SolicitacaoParticipacao {
    
    private final UUID solicitacaoId;
    private final UUID participanteId;
    private final UUID eventoId;
    private boolean aceita;

    public SolicitacaoParticipacao(UUID solicitacaoId, UUID participanteId, UUID eventoId) {
        this.solicitacaoId = solicitacaoId;
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.aceita = false;
    }

    public void aprovar() {
        this.aceita = true;
    }

    public UUID getSolicitacaoId() { return solicitacaoId; }
    public UUID getParticipanteId() { return participanteId; }
    public UUID getEventoId() { return eventoId; }
    public boolean isAceita() { return aceita; }
}
