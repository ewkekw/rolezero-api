package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record JulgarSolicitacaoRequest(
    @NotNull(message = "A decisão (aprovada/recusada) deve ser informada.")
    Boolean aprovada
) {}
