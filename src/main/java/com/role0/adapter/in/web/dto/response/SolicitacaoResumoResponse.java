package com.role0.adapter.in.web.dto.response;

import java.util.UUID;

public record SolicitacaoResumoResponse(
    UUID solicitacaoId,
    UUID usuarioId,
    double trustScore
) {}
