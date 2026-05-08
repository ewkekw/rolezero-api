package com.role0.core.application.usecase;

import java.util.UUID;

public interface CancelarEventoUseCase {
    void executar(UUID eventoId, UUID hostId);
}
