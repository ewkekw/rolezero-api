package com.role0.core.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.role0.adapter.in.web.dto.response.MeuEventoResponse;
import com.role0.adapter.out.persistence.entity.EventoJpaEntity;
import com.role0.core.application.port.out.ListarMeusEventosQueryPort;
import com.role0.core.domain.evento.valueobject.StatusEvento;

@ExtendWith(MockitoExtension.class)
class ListarMeusEventosServiceTest {

    @Mock
    private ListarMeusEventosQueryPort queryPort;

    @InjectMocks
    private ListarMeusEventosService service;

    @Test
    @SuppressWarnings("null")
    void deveMapearEventosCorretamenteParaHostEParticipante() {
        UUID myId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        
        EventoJpaEntity e1 = new EventoJpaEntity();
        e1.setId(UUID.randomUUID());
        e1.setTitulo("Meu Evento");
        e1.setHostId(myId);
        e1.setStatus(StatusEvento.ABERTO_PARA_VAGAS);
        e1.setHorarioInicio(LocalDateTime.now().plusDays(1));

        EventoJpaEntity e2 = new EventoJpaEntity();
        e2.setId(UUID.randomUUID());
        e2.setTitulo("Outro Evento");
        e2.setHostId(otherId); // não sou host, sou participante
        e2.setStatus(StatusEvento.EXPIRADO);
        e2.setHorarioInicio(LocalDateTime.now().minusDays(1));

        when(queryPort.buscarMeusEventos(myId)).thenReturn(List.of(e1, e2));

        List<MeuEventoResponse> resultados = service.executar(myId);

        assertEquals(2, resultados.size());
        
        MeuEventoResponse resp1 = resultados.get(0);
        assertEquals(e1.getId(), resp1.id());
        assertEquals("Meu Evento", resp1.titulo());
        assertEquals(true, resp1.isHost());
        
        MeuEventoResponse resp2 = resultados.get(1);
        assertEquals(e2.getId(), resp2.id());
        assertEquals("Outro Evento", resp2.titulo());
        assertEquals(false, resp2.isHost()); // não é o host
        
        verify(queryPort).buscarMeusEventos(myId);
    }
}
