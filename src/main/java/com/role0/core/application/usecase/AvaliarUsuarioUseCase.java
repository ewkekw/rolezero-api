package com.role0.core.application.usecase;

import java.util.UUID;

import org.springframework.lang.NonNull;

import com.role0.adapter.in.web.dto.request.AvaliarUsuarioRequest;

public interface AvaliarUsuarioUseCase {
    void executar(@NonNull UUID avaliadorId, @NonNull UUID avaliadoId, AvaliarUsuarioRequest request);
}
