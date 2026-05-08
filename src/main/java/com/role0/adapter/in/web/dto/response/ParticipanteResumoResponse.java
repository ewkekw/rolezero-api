package com.role0.adapter.in.web.dto.response;

import java.util.List;
import java.util.UUID;

public record ParticipanteResumoResponse(
    UUID usuarioId,
    String nomeDisplay,
    double trustScore,
    List<String> vibes
) {}
