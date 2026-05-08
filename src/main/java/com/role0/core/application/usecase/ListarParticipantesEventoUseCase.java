package com.role0.core.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import com.role0.adapter.in.web.dto.response.ParticipanteResumoResponse;

public interface ListarParticipantesEventoUseCase {
    List<ParticipanteResumoResponse> executar(@NonNull UUID eventoId, @NonNull UUID solicitanteId);
}
