package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import com.role0.core.domain.usuario.valueobject.VibeTag;

public record UpdateVibeTagsRequest(
    @NotEmpty(message = "As tags não podem ser vazias.")
    @Size(max = 5, message = "Você pode escolher no máximo 5 Vibe Tags.")
    List<VibeTag> tags
) {}
