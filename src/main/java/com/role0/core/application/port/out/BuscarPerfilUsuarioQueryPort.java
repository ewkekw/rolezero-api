package com.role0.core.application.port.out;

import java.util.Optional;
import java.util.UUID;
import com.role0.adapter.in.web.dto.response.UsuarioPerfilResponse;

public interface BuscarPerfilUsuarioQueryPort {
    Optional<UsuarioPerfilResponse> buscarPerfil(UUID id);
}
