package com.role0.core.application.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

import com.role0.core.application.event.AnfitriaoInterinoPromovidoEvent;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.usecase.CancelarEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.usuario.entity.Usuario;

import org.springframework.stereotype.Service;

@Service
public class CancelarEventoService implements CancelarEventoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CancelarEventoService(EventoRepositoryPort eventoRepository, UsuarioRepositoryPort usuarioRepository, ApplicationEventPublisher eventPublisher) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void executar(UUID eventoId, UUID hostId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento não encontrado"));

        if (!evento.getHostId().equals(hostId)) {
            throw new AccessDeniedException("Apenas o host atual pode cancelar o evento.");
        }

        LocalDateTime agora = LocalDateTime.now();
        long horasParaInicio = ChronoUnit.HOURS.between(agora, evento.getHorarioInicio());

        UUID interinoId = null;

        if (horasParaInicio < 2 && !evento.getParticipantesAprovados().isEmpty()) {
            Optional<Usuario> melhorSubstitutoOpt = usuarioRepository.buscarMelhorSubstituto(evento.getParticipantesAprovados());
            if (melhorSubstitutoOpt.isPresent()) {
                interinoId = melhorSubstitutoOpt.get().getId();
            }
        }

        evento.cancelarPeloHost(hostId, interinoId, agora);
        
        eventoRepository.salvar(evento);

        if (interinoId != null) {
            // Disparar o domain event
            AnfitriaoInterinoPromovidoEvent event = new AnfitriaoInterinoPromovidoEvent(
                evento.getId(),
                hostId,
                interinoId,
                agora
            );
            eventPublisher.publishEvent(event);
        }
    }
}
