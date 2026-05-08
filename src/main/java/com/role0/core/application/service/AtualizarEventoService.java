package com.role0.core.application.service;

import java.util.UUID;

import com.role0.core.application.dto.AtualizarEventoCommand;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.usecase.AtualizarEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;

import org.springframework.stereotype.Service;

@Service
public class AtualizarEventoService implements AtualizarEventoUseCase {

    private final EventoRepositoryPort eventoRepository;

    public AtualizarEventoService(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public void executar(UUID eventoId, AtualizarEventoCommand command, UUID usuarioAutenticadoId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento não encontrado."));

        if (!evento.getHostId().equals(usuarioAutenticadoId)) {
            throw new org.springframework.security.access.AccessDeniedException("Apenas o host pode atualizar os detalhes do evento.");
        }

        int totalAprovadosAtual = evento.getParticipantesAprovados().size();

        evento.atualizarDetalhes(command.titulo(), command.descricao(), command.maxCapacity(), totalAprovadosAtual);

        eventoRepository.salvar(evento);
    }
}
