package com.role0.core.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import com.role0.adapter.in.web.dto.response.AvaliacaoResponse;
import com.role0.adapter.in.web.dto.response.PerfilReviewsResponse;
import com.role0.adapter.out.persistence.entity.PerfilReputacaoJpaEntity;
import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataAvaliacaoRepository;
import com.role0.adapter.out.persistence.repository.SpringDataPerfilReputacaoRepository;
import com.role0.adapter.out.persistence.repository.SpringDataUsuarioRepository;
import com.role0.core.application.usecase.BuscarAvaliacoesUsuarioUseCase;

@Service
public class BuscarAvaliacoesUsuarioService implements BuscarAvaliacoesUsuarioUseCase {

    private final SpringDataAvaliacaoRepository avaliacaoRepository;
    private final SpringDataPerfilReputacaoRepository reputacaoRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    public BuscarAvaliacoesUsuarioService(
            SpringDataAvaliacaoRepository avaliacaoRepository,
            SpringDataPerfilReputacaoRepository reputacaoRepository,
            SpringDataUsuarioRepository usuarioRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.reputacaoRepository = reputacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @SuppressWarnings("null")
    public PerfilReviewsResponse executar(@NonNull UUID usuarioId, int limit) {
        int pageSize = Math.min(limit, 100);

        PerfilReputacaoJpaEntity reputacao = reputacaoRepository.findByUsuarioId(usuarioId).orElse(null);
        BigDecimal trustScore = reputacao != null ? reputacao.getCurrentScore() : BigDecimal.valueOf(5.0);
        long total = reputacao != null ? reputacao.getTotalAvaliacoes() : 0;

        List<AvaliacaoResponse> avaliacoes = avaliacaoRepository
                .findByAvaliadoIdOrderByCreatedAtDesc(usuarioId, PageRequest.of(0, pageSize))
                .stream()
                .map(a -> {
                    String nomeAvaliador = usuarioRepository.findById(a.getAvaliadorId())
                            .map(UsuarioJpaEntity::getNome).orElse("Usuário");
                    return new AvaliacaoResponse(
                            a.getId(),
                            a.getAvaliadorId(),
                            nomeAvaliador,
                            a.getNota(),
                            a.getComentario(),
                            a.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        return new PerfilReviewsResponse(trustScore, total, avaliacoes);
    }
}
