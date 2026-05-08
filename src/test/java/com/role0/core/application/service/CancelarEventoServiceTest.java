package com.role0.core.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

import com.role0.core.application.event.AnfitriaoInterinoPromovidoEvent;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.evento.valueobject.StatusEvento;
import com.role0.core.domain.usuario.entity.Usuario;

@ExtendWith(MockitoExtension.class)
class CancelarEventoServiceTest {

    @Mock
    private EventoRepositoryPort eventoRepository;
    
    @Mock
    private UsuarioRepositoryPort usuarioRepository;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CancelarEventoService cancelarEventoService;

    @Test
    @SuppressWarnings("null")
    void nadeDeveCancelarSeNaoForHost() {
        UUID eventoId = UUID.randomUUID();
        UUID intrusoId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();

        Evento evento = new Evento(eventoId, hostId, "Meu Role", 10, new CoordenadaGeografica(0, 0), LocalDateTime.now().plusHours(5));
        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            cancelarEventoService.executar(eventoId, intrusoId);
        });
        org.junit.jupiter.api.Assertions.assertNotNull(ex);
    }

    @Test
    @SuppressWarnings("null")
    void deveCancelarDiretoSemInterinoSeFaltaMuitoTempo() {
        UUID eventoId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();

        Evento evento = new Evento(eventoId, hostId, "Meu Role", 10, new CoordenadaGeografica(0, 0), LocalDateTime.now().plusHours(5));
        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));

        cancelarEventoService.executar(eventoId, hostId);

        assertEquals(StatusEvento.EXPIRADO, evento.getStatus());
        verify(usuarioRepository, never()).buscarMelhorSubstituto(anyList());
        verify(eventPublisher, never()).publishEvent(any(Object.class));
        verify(eventoRepository).salvar(evento);
    }

    @Test
    @SuppressWarnings("null")
    void devePromoverInterinoSeFaltaMenosDeDuasHoras() {
        UUID eventoId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();
        UUID substitutoId = UUID.randomUUID();

        Evento evento = new Evento(eventoId, hostId, "Meu Role", 10, new CoordenadaGeografica(0, 0), LocalDateTime.now().plusHours(1));
        evento.setStatus(StatusEvento.ABERTO_PARA_VAGAS);
        evento.aprovarParticipante(substitutoId);

        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));

        Usuario substituto = new Usuario(substitutoId, "Cara Responsável");
        when(usuarioRepository.buscarMelhorSubstituto(anyList())).thenReturn(Optional.of(substituto));

        cancelarEventoService.executar(eventoId, hostId);

        assertEquals(StatusEvento.ABERTO_PARA_VAGAS, evento.getStatus());
        assertEquals(substitutoId, evento.getHostId());
        
        verify(eventoRepository).salvar(evento);
        verify(eventPublisher).publishEvent(any(AnfitriaoInterinoPromovidoEvent.class));
    }
}
