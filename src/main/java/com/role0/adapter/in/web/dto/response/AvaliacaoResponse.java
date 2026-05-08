package com.role0.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvaliacaoResponse(
    UUID id,
    UUID avaliadorId,
    String avaliadorNome,
    int nota,
    String comentario,
    LocalDateTime criadoEm
) {}
