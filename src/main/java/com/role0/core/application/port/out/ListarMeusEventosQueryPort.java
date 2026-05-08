package com.role0.core.application.port.out;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import com.role0.adapter.out.persistence.entity.EventoJpaEntity;

public interface ListarMeusEventosQueryPort {
    List<EventoJpaEntity> buscarMeusEventos(@NonNull UUID usuarioId);
}
