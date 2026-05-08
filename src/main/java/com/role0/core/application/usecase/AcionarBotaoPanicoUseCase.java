package com.role0.core.application.usecase;

import java.util.UUID;

public interface AcionarBotaoPanicoUseCase {
    void executar(UUID eventoId, UUID reportadorId);
}
