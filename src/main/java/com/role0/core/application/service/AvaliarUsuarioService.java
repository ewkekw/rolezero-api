package com.role0.core.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.role0.adapter.in.web.dto.request.AvaliarUsuarioRequest;
import com.role0.adapter.out.persistence.entity.AvaliacaoJpaEntity;
import com.role0.adapter.out.persistence.entity.PerfilReputacaoJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataAvaliacaoRepository;
import com.role0.adapter.out.persistence.repository.SpringDataPerfilReputacaoRepository;
import com.role0.core.application.usecase.AvaliarUsuarioUseCase;

@Service
public class AvaliarUsuarioService implements AvaliarUsuarioUseCase {

    private final SpringDataAvaliacaoRepository avaliacaoRepository;
    private final SpringDataPerfilReputacaoRepository reputacaoRepository;

    public AvaliarUsuarioService(
            SpringDataAvaliacaoRepository avaliacaoRepository,
            SpringDataPerfilReputacaoRepository reputacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.reputacaoRepository = reputacaoRepository;
    }

    @Override
    @Transactional
    public void executar(@NonNull UUID avaliadorId, @NonNull UUID avaliadoId, AvaliarUsuarioRequest req) {
        if (avaliadorId.equals(avaliadoId)) {
            throw new IllegalArgumentException("Um usuário não pode avaliar a si mesmo.");
        }

        AvaliacaoJpaEntity avaliacao = new AvaliacaoJpaEntity();
        avaliacao.setId(UUID.randomUUID());
        avaliacao.setAvaliadorId(avaliadorId);
        avaliacao.setAvaliadoId(avaliadoId);
        avaliacao.setEventoId(req.eventoId());
        avaliacao.setNota(req.nota());
        avaliacao.setComentario(req.comentario());
        avaliacao.setCreatedAt(LocalDateTime.now());
        avaliacaoRepository.save(avaliacao);

        // Recalcular TrustScore de forma incremental (weighted running average)
        PerfilReputacaoJpaEntity reputacao = reputacaoRepository.findByUsuarioId(avaliadoId)
                .orElseGet(() -> new PerfilReputacaoJpaEntity(avaliadoId, BigDecimal.valueOf(5.0), 0));

        int novasTotais = reputacao.getTotalAvaliacoes() + 1;
        BigDecimal novoScore = reputacao.getCurrentScore()
                .multiply(BigDecimal.valueOf(reputacao.getTotalAvaliacoes()))
                .add(BigDecimal.valueOf(req.nota()))
                .divide(BigDecimal.valueOf(novasTotais), 2, RoundingMode.HALF_UP);

        reputacao.setCurrentScore(novoScore);
        reputacao.setTotalAvaliacoes(novasTotais);
        reputacaoRepository.save(reputacao);
    }
}
