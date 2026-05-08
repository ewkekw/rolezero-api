package com.role0.adapter.in.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UsuarioPublicoResponse(
        UUID id,
        String nomeDisplay,
        List<String> vibes,
        boolean isProvedIdentityToken,
        BigDecimal trustScore,
        int qtdAvaliacoes
) {}
