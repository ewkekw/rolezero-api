package com.role0.core.domain.evento;

import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.evento.valueobject.StatusEvento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventoTest {

    @Test
    void naoDevePermitirAprovarParticipanteAlemDaCapacidade() {
        Evento evento = new Evento(UUID.randomUUID(), UUID.randomUUID(), "Rolê", 2, new CoordenadaGeografica(0,0), LocalDateTime.now().plusHours(3));
        evento.setStatus(StatusEvento.ABERTO_PARA_VAGAS);
        
        evento.aprovarParticipante(UUID.randomUUID());
        evento.aprovarParticipante(UUID.randomUUID());
        
        EventoDomainException exception = assertThrows(
            EventoDomainException.class,
            () -> evento.aprovarParticipante(UUID.randomUUID())
        );
        assertEquals("O evento não está aceitando novos participantes.", exception.getMessage());
    }
    
    @Test
    void deveValidarCheckInApenasDentroDoRaioDe50Metros() {
        CoordenadaGeografica localDoRole = new CoordenadaGeografica(-23.550520, -46.633308); // Sé, SP
        Evento evento = new Evento(UUID.randomUUID(), UUID.randomUUID(), "Pico", 10, localDoRole, LocalDateTime.now());
        
        // Coordenada exata
        assertTrue(evento.validarProximidadeCheckIn(new CoordenadaGeografica(-23.550520, -46.633308)));
        
        // Coordenada muito distante
        assertFalse(evento.validarProximidadeCheckIn(new CoordenadaGeografica(-22.906847, -43.172896))); // RJ
    }
    
    @Test
    void devePromoverParticipanteAHostInterinoSeCanceladoMenosDeDuasHorasValido() {
        UUID hostOriginal = UUID.randomUUID();
        UUID subHostId = UUID.randomUUID();
        
        Evento evento = new Evento(UUID.randomUUID(), hostOriginal, "Rolê", 10, new CoordenadaGeografica(0,0), LocalDateTime.now().plusHours(1));
        evento.setStatus(StatusEvento.ABERTO_PARA_VAGAS);
        evento.aprovarParticipante(subHostId);
        
        evento.cancelarPeloHost(hostOriginal, subHostId);
        
        assertEquals(subHostId, evento.getHostId());
        assertEquals(StatusEvento.ABERTO_PARA_VAGAS, evento.getStatus()); // O evento sobreviveu
    }
    
    @Test
    void deveCancelarDiretoSeFaltarMaisDeDuasHoras() {
        UUID hostOriginal = UUID.randomUUID();
        Evento evento = new Evento(UUID.randomUUID(), hostOriginal, "Rolê", 10, new CoordenadaGeografica(0,0), LocalDateTime.now().plusHours(3));
        
        evento.cancelarPeloHost(hostOriginal, null);
        
        assertEquals(StatusEvento.EXPIRADO, evento.getStatus()); // Evento morre
    }
}
