package com.role0.core.application.usecase;

import java.util.UUID;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;

public interface RealizarCheckInUseCase {
    void executar(UUID eventoId, UUID participanteId, CoordenadaGeografica coordenadaUsuario);
}
