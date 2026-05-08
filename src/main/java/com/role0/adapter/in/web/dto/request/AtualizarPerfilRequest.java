package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import com.role0.core.domain.usuario.valueobject.VibeTag;

public record AtualizarPerfilRequest(
    @NotBlank(message = "O nome de exibição não pode ser vazio.")
    String nomeDisplay,
    Set<VibeTag> vibeTags
) {}
