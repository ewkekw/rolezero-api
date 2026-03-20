package com.role0.core.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.role0.adapter.in.web.dto.response.ParticipanteResumoResponse;
import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataUsuarioRepository;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.ListarSolicitacoesQueryPort;
import com.role0.core.application.usecase.ListarParticipantesEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoNaoEncontradoException;

import java.util.Optional;

@Service
public class ListarParticipantesEventoService implements ListarParticipantesEventoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final ListarSolicitacoesQueryPort listQueryPort;
    private final SpringDataUsuarioRepository usuarioRepository;

    public ListarParticipantesEventoService(
            EventoRepositoryPort eventoRepository,
            ListarSolicitacoesQueryPort listQueryPort,
            SpringDataUsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.listQueryPort = listQueryPort;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @SuppressWarnings("null")
    public List<ParticipanteResumoResponse> executar(@NonNull UUID eventoId, @NonNull UUID solicitanteId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException(eventoId.toString()));

        // Verifica se o solicitante é host ou participante aprovado
        boolean isHost = evento.getHostId().equals(solicitanteId);
        boolean isParticipante = listQueryPort.listarAprovadosPorEvento(eventoId).stream()
                .anyMatch(s -> s.getUsuarioId().equals(solicitanteId));

        if (!isHost && !isParticipante) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Apenas participantes aprovados e o anfitrião podem ver a lista de participantes.");
        }

        return listQueryPort.listarAprovadosPorEvento(eventoId).stream()
                .map(s -> {
                    Optional<UsuarioJpaEntity> usuarioOpt = usuarioRepository.findById(s.getUsuarioId());
                    String nome = usuarioOpt.map(UsuarioJpaEntity::getNome).orElse("Usuário");
                    @SuppressWarnings("null")
                    List<String> vibes = usuarioOpt
                            .map(u -> u.getTags().stream().map(Enum::name).collect(Collectors.toList()))
                            .orElse(List.of());
                    return new ParticipanteResumoResponse(
                            s.getUsuarioId(),
                            nome,
                            0.0, // trustScore enriquecido futuramente
                            vibes
                    );
                })
                .collect(Collectors.toList());
    }
}
