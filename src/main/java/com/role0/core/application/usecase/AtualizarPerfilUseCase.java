package com.role0.core.application.usecase;

import java.util.Set;
import java.util.UUID;
import com.role0.core.domain.usuario.valueobject.VibeTag;

public interface AtualizarPerfilUseCase {
    void executar(UUID usuarioId, String novoNomeDisplay, Set<VibeTag> vibeTags);
}
