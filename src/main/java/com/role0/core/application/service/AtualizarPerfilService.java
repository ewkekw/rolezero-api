package com.role0.core.application.service;

import java.util.UUID;

import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.usecase.AtualizarPerfilUseCase;
import com.role0.core.domain.usuario.entity.Usuario;
import com.role0.core.domain.usuario.exception.UsuarioDomainException;

import org.springframework.stereotype.Service;

@Service
public class AtualizarPerfilService implements AtualizarPerfilUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public AtualizarPerfilService(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void executar(UUID usuarioId, String novoNomeDisplay) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioDomainException("Usuário não encontrado."));

        usuario.mudarNome(novoNomeDisplay);

        usuarioRepository.salvar(usuario);
    }
}
