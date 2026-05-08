package com.role0.core.application.usecase;

import com.role0.core.application.dto.EventoDetalheOutput;
import java.util.UUID;

public interface BuscarEventoUseCase {
    EventoDetalheOutput executar(UUID eventId);
}
