package com.role0.core.application.port.out;

import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;

import com.role0.adapter.in.web.dto.response.UsuarioPublicoResponse;

public interface BuscarPerfilPublicoQueryPort {
    Optional<UsuarioPublicoResponse> buscarPerfilPublico(@NonNull UUID id);
}
