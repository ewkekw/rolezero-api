package com.role0.core.application.usecase;

import java.util.UUID;

public interface ProcessarSolicitacaoUseCase {
    void aprovar(UUID eventoId, UUID hostId, UUID solicitacaoId);
    void recusar(UUID eventoId, UUID hostId, UUID solicitacaoId);
}
