package com.role0.core.application.service;

import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.role0.adapter.in.web.dto.response.UsuarioPublicoResponse;
import com.role0.core.application.port.out.BuscarPerfilPublicoQueryPort;
import com.role0.core.application.usecase.BuscarPerfilPublicoUseCase;
import com.role0.core.domain.usuario.exception.UsuarioNaoEncontradoException;

@Service
public class BuscarPerfilPublicoService implements BuscarPerfilPublicoUseCase {

    private final BuscarPerfilPublicoQueryPort queryPort;

    public BuscarPerfilPublicoService(BuscarPerfilPublicoQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Override
    public UsuarioPublicoResponse executar(@NonNull UUID userId) {
        return queryPort.buscarPerfilPublico(userId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado: " + userId));
    }
}
