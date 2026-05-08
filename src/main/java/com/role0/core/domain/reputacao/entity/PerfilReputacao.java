package com.role0.core.domain.reputacao.entity;

import java.util.UUID;
import com.role0.core.domain.reputacao.valueobject.TrustScore;

public class PerfilReputacao {
    private final UUID usuarioId;
    private TrustScore scoreAtual;

    public PerfilReputacao(UUID usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("UsuarioID é obrigatório para PerfilReputacao");
        this.usuarioId = usuarioId;
        // Todo usuário começa com score neutro de 5.0 e 0 avaliações
        this.scoreAtual = new TrustScore(5.0, 0); 
    }

    public void processarNovaAvaliacao(double nota) {
        this.scoreAtual = this.scoreAtual.adicionarAvaliacao(nota);
    }

    public UUID getUsuarioId() { return usuarioId; }
    public TrustScore getScoreAtual() { return scoreAtual; }
}
