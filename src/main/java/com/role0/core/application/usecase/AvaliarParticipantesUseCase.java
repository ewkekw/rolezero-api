package com.role0.core.application.usecase;

import java.util.UUID;

public interface AvaliarParticipantesUseCase {
    void executar(UUID eventoId, UUID avaliadorId, UUID avaliadoId, double nota);
}
