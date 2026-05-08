package com.role0.core.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.security.access.AccessDeniedException;

import com.role0.core.application.dto.AtualizarEventoCommand;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;

@ExtendWith(MockitoExtension.class)
class AtualizarEventoServiceTest {

    @Mock
    private EventoRepositoryPort eventoRepository;
    
    @InjectMocks
    private AtualizarEventoService atualizarEventoService;

    @Test
    void naoDevePermitirAtualizarSeUsuarioNaoForHost() {
        UUID eventoId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();
        UUID intrusoId = UUID.randomUUID();

        Evento evento = new Evento(eventoId, hostId, "Meu Role", 10, new CoordenadaGeografica(0, 0), LocalDateTime.now().plusHours(5));
        
        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));

        AtualizarEventoCommand command = new AtualizarEventoCommand("Novo Titulo", "Nova Desc", 5);

        var exception = assertThrows(AccessDeniedException.class, () -> {
            atualizarEventoService.executar(eventoId, command, intrusoId);
        });

        assertEquals("Apenas o host pode atualizar os detalhes do evento.", exception.getMessage());
    }

    @Test
    void devePermitirAtualizarSeForHost() {
        UUID eventoId = UUID.randomUUID();
        UUID hostId = UUID.randomUUID();

        Evento evento = new Evento(eventoId, hostId, "Meu Role", 10, new CoordenadaGeografica(0, 0), LocalDateTime.now().plusHours(5));
        
        when(eventoRepository.buscarPorId(eventoId)).thenReturn(Optional.of(evento));

        AtualizarEventoCommand command = new AtualizarEventoCommand("Titulo Atualizado", "Descricao Legal", 15);

        atualizarEventoService.executar(eventoId, command, hostId);

        assertEquals("Titulo Atualizado", evento.getTitulo());
        assertEquals("Descricao Legal", evento.getDescricao());
        assertEquals(15, evento.getCapacidadeMaxima());
        verify(eventoRepository).salvar(evento);
    }
}
