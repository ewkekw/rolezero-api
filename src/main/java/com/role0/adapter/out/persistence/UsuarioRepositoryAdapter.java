package com.role0.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

// import com.role0.adapter.out.persistence.repository.SpringDataUsuarioRepository;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.domain.usuario.entity.Usuario;

import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.adapter.out.persistence.mapper.PersistenceMapper;
import com.role0.adapter.out.persistence.repository.SpringDataUsuarioRepository;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository repository;
    private final PersistenceMapper mapper;

    public UsuarioRepositoryAdapter(SpringDataUsuarioRepository repository, PersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @SuppressWarnings("null")
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entity = mapper.toJpaEntity(usuario);
        UsuarioJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @SuppressWarnings("null")
    public Optional<Usuario> buscarPorId(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    @SuppressWarnings("null")
    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarMelhorSubstituto(java.util.List<UUID> candidatosId) {
        if (candidatosId == null || candidatosId.isEmpty()) {
            return Optional.empty();
        }
        return repository.findBestSubstituteIn(candidatosId).map(mapper::toDomain);
    }
}
