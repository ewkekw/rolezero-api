package com.role0.core.application.usecase;

import java.util.UUID;

import com.role0.core.application.dto.AtualizarEventoCommand;

public interface AtualizarEventoUseCase {
    void executar(UUID eventoId, AtualizarEventoCommand command, UUID usuarioAutenticadoId);
}
