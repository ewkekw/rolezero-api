package com.role0.core.application.service;

import java.util.UUID;

import com.role0.adapter.in.web.dto.response.UsuarioPerfilResponse;
import com.role0.core.application.port.out.BuscarPerfilUsuarioQueryPort;
import com.role0.core.application.usecase.BuscarPerfilUsuarioUseCase;
import com.role0.core.domain.usuario.exception.UsuarioDomainException;

import org.springframework.stereotype.Service;

@Service
public class BuscarPerfilUsuarioService implements BuscarPerfilUsuarioUseCase {

    private final BuscarPerfilUsuarioQueryPort queryPort;

    public BuscarPerfilUsuarioService(BuscarPerfilUsuarioQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Override
    public UsuarioPerfilResponse executar(UUID usuarioId) {
        return queryPort.buscarPerfil(usuarioId)
                .orElseThrow(() -> new UsuarioDomainException("Perfil do usuário não encontrado."));
    }
}
