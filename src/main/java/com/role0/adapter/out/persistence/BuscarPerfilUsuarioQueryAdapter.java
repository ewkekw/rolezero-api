package com.role0.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.role0.adapter.in.web.dto.response.UsuarioPerfilResponse;
import com.role0.adapter.in.web.dto.response.UsuarioPublicoResponse;
import com.role0.adapter.out.persistence.entity.PerfilReputacaoJpaEntity;
import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataPerfilReputacaoRepository;
import com.role0.adapter.out.persistence.repository.SpringDataUsuarioRepository;
import com.role0.core.application.port.out.BuscarPerfilPublicoQueryPort;
import com.role0.core.application.port.out.BuscarPerfilUsuarioQueryPort;

@Component
public class BuscarPerfilUsuarioQueryAdapter implements BuscarPerfilUsuarioQueryPort, BuscarPerfilPublicoQueryPort {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataPerfilReputacaoRepository reputacaoRepository;

    public BuscarPerfilUsuarioQueryAdapter(SpringDataUsuarioRepository usuarioRepository, SpringDataPerfilReputacaoRepository reputacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reputacaoRepository = reputacaoRepository;
    }

    @Override
    @SuppressWarnings("null")
    public Optional<UsuarioPerfilResponse> buscarPerfil(UUID id) {
        Optional<UsuarioJpaEntity> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        UsuarioJpaEntity usuario = usuarioOpt.get();
        if (usuario.getId() == null) {
             return Optional.empty();
        }
        Optional<PerfilReputacaoJpaEntity> reputacaoOpt = reputacaoRepository.findByUsuarioId(id);

        @SuppressWarnings("null")
        UsuarioPerfilResponse response = new UsuarioPerfilResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTags().stream().map(Enum::name).collect(Collectors.toList()),
            usuario.isProvedIdentityToken(),
            reputacaoOpt.map(PerfilReputacaoJpaEntity::getCurrentScore).orElse(java.math.BigDecimal.ZERO)
        );
        return Optional.of(response);
    }

    @Override
    @SuppressWarnings("null")
    public Optional<UsuarioPublicoResponse> buscarPerfilPublico(@NonNull UUID id) {
        Optional<UsuarioJpaEntity> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        UsuarioJpaEntity usuario = usuarioOpt.get();
        if (usuario.getId() == null) {
             return Optional.empty();
        }
        Optional<PerfilReputacaoJpaEntity> reputacaoOpt = reputacaoRepository.findByUsuarioId(id);

        @SuppressWarnings("null")
        UsuarioPublicoResponse response = new UsuarioPublicoResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getTags().stream().map(Enum::name).collect(Collectors.toList()),
            usuario.isProvedIdentityToken(),
            reputacaoOpt.map(PerfilReputacaoJpaEntity::getCurrentScore).orElse(java.math.BigDecimal.ZERO),
            reputacaoOpt.map(PerfilReputacaoJpaEntity::getTotalAvaliacoes).orElse(0)
        );
        return Optional.of(response);
    }
}
