package com.role0.core.application.port.out;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.usuario.valueobject.VibeTag;

public interface EventoRepositoryPort {
    Evento salvar(Evento evento);
    Optional<Evento> buscarPorId(UUID id);
    List<Evento> buscarEventosProximos(CoordenadaGeografica localizacao, double raioKm, List<VibeTag> tagsFilter);
    int contarParticipantesAprovados(UUID eventoId);
}
