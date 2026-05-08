package com.role0.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.role0.adapter.out.persistence.entity.EventoJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataEventoRepository;
import com.role0.core.application.port.out.ListarMeusEventosQueryPort;

@Component
public class ListarMeusEventosQueryAdapter implements ListarMeusEventosQueryPort {

    private final SpringDataEventoRepository repository;

    public ListarMeusEventosQueryAdapter(SpringDataEventoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EventoJpaEntity> buscarMeusEventos(@NonNull UUID usuarioId) {
        return repository.findMeusEventos(usuarioId);
    }
}
