package com.role0.adapter.in.web.dto.response;

import java.util.List;
import java.math.BigDecimal;

public record PerfilReviewsResponse(
    BigDecimal trustScore,
    long totalAvaliacoes,
    List<AvaliacaoResponse> avaliacoes
) {}
