package com.role0.adapter.in.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UsuarioPerfilResponse(
    UUID id,
    String nome,
    String email,
    List<String> vibes,
    boolean biometriaValidada,
    BigDecimal trustScore
) {}
