package com.role0.core.application.usecase;

import java.util.UUID;
import com.role0.core.domain.evento.valueobject.SolicitacaoParticipacao;

public interface SolicitarParticipacaoUseCase {
    SolicitacaoParticipacao executar(UUID eventoId, UUID participanteId);
}
