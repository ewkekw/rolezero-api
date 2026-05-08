package com.role0.core.application.usecase;

import java.util.UUID;
import com.role0.adapter.in.web.dto.response.UsuarioPerfilResponse;

public interface BuscarPerfilUsuarioUseCase {
    UsuarioPerfilResponse executar(UUID usuarioId);
}
