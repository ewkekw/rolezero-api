package com.role0.core.application.service;

import java.util.UUID;

import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.usecase.RealizarCheckInUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;

import org.springframework.stereotype.Service;

@Service
public class RealizarCheckInService implements RealizarCheckInUseCase {

    private final EventoRepositoryPort eventoRepository;

    public RealizarCheckInService(EventoRepositoryPort eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public void executar(UUID eventoId, UUID participanteId, CoordenadaGeografica coordenadaUsuario) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento não encontrado."));
            
        if (!evento.getParticipantesAprovados().contains(participanteId)) {
            throw new EventoDomainException("Você não possui vaga reservada neste rolê.");
        }

        // Validação Geo-Espacial encapsulada pura no Domínio
        boolean estaPorPerto = evento.validarProximidadeCheckIn(coordenadaUsuario);
        if (!estaPorPerto) {
            throw new EventoDomainException("Fora da área de alcance. Por favor, aproxime-se (50 metros) do local do evento para efetuar o Check-in.");
        }
        
        // Check-in efetuado (gravação no banco de Presença Confirmada delegada à persistência real)
    }
}
