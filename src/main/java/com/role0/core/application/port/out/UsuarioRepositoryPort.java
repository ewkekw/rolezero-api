package com.role0.core.application.port.out;

import java.util.Optional;
import java.util.UUID;
import com.role0.core.domain.usuario.entity.Usuario;

public interface UsuarioRepositoryPort {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(UUID id);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarMelhorSubstituto(java.util.List<UUID> candidatosId);
}
