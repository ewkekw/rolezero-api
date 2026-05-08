package com.role0.core.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;

public interface CriarEventoUseCase {
    Evento executar(UUID hostId, String titulo, int capacidadeMaxima, CoordenadaGeografica localizacao, LocalDateTime horarioInicio);
}
