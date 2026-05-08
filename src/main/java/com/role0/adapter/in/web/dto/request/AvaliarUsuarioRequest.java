package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AvaliarUsuarioRequest(
    @NotNull UUID eventoId,
    @NotNull @Min(1) @Max(5) Integer nota,
    String comentario
) {}
