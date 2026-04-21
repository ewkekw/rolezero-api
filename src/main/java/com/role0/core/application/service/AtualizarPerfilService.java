package com.role0.core.application.service;

import java.util.Set;
import java.util.UUID;

import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.usecase.AtualizarPerfilUseCase;
import com.role0.core.domain.usuario.entity.Usuario;
import com.role0.core.domain.usuario.exception.UsuarioDomainException;
import com.role0.core.domain.usuario.valueobject.VibeTag;

import org.springframework.stereotype.Service;

@Service
public class AtualizarPerfilService implements AtualizarPerfilUseCase {

    private final UsuarioRepositoryPort usuarioRepository;

    public AtualizarPerfilService(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void executar(UUID usuarioId, String novoNomeDisplay, Set<VibeTag> vibeTags) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioDomainException("Usuário não encontrado."));

        usuario.mudarNome(novoNomeDisplay);
        if (vibeTags != null) {
            usuario.setVibeTags(vibeTags);
        }

        usuarioRepository.salvar(usuario);
    }
}
