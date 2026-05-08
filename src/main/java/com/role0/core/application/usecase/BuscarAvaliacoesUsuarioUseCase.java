package com.role0.core.application.usecase;

import java.util.UUID;

import org.springframework.lang.NonNull;

import com.role0.adapter.in.web.dto.response.PerfilReviewsResponse;

public interface BuscarAvaliacoesUsuarioUseCase {
    PerfilReviewsResponse executar(@NonNull UUID usuarioId, int limit);
}
