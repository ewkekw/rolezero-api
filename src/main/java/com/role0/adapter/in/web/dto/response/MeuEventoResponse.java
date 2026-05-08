package com.role0.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import com.role0.core.domain.evento.valueobject.StatusEvento;

public record MeuEventoResponse(
    UUID id,
    String titulo,
    LocalDateTime horarioInicio,
    StatusEvento status,
    boolean isHost
) {}
