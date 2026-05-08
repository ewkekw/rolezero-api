package com.role0.core.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.role0.adapter.in.web.dto.response.SolicitacaoResumoResponse;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.ListarSolicitacoesQueryPort;
import com.role0.core.application.usecase.ListarSolicitacoesEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoNaoEncontradoException;
import com.role0.core.domain.evento.valueobject.StatusSolicitacao;

@Service
public class ListarSolicitacoesEventoService implements ListarSolicitacoesEventoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final ListarSolicitacoesQueryPort listQueryPort;

    public ListarSolicitacoesEventoService(
            EventoRepositoryPort eventoRepository,
            ListarSolicitacoesQueryPort listQueryPort) {
        this.eventoRepository = eventoRepository;
        this.listQueryPort = listQueryPort;
    }

    @Override
    public List<SolicitacaoResumoResponse> executar(@NonNull UUID eventoId, @NonNull UUID hostId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException(eventoId.toString()));

        if (!evento.getHostId().equals(hostId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Apenas o anfitrião pode visualizar as solicitações.");
        }

        return listQueryPort.listarPorEventoEStatus(eventoId, StatusSolicitacao.PENDENTE).stream()
                .map(s -> new SolicitacaoResumoResponse(
                        s.getId(),
                        s.getUsuarioId(),
                        0.0 // trustScore enriquecido na próxima iteração
                ))
                .collect(Collectors.toList());
    }
}
