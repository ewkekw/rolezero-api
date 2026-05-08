package com.role0.core.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.role0.adapter.in.web.dto.response.MeuEventoResponse;
import com.role0.core.application.port.out.ListarMeusEventosQueryPort;
import com.role0.core.application.usecase.ListarMeusEventosUseCase;

@Service
public class ListarMeusEventosService implements ListarMeusEventosUseCase {

    private final ListarMeusEventosQueryPort queryPort;

    public ListarMeusEventosService(ListarMeusEventosQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Override
    public List<MeuEventoResponse> executar(@NonNull UUID usuarioId) {
        return queryPort.buscarMeusEventos(usuarioId).stream()
                .map(e -> new MeuEventoResponse(
                        e.getId(),
                        e.getTitulo(),
                        e.getHorarioInicio(),
                        e.getStatus(),
                        e.getHostId().equals(usuarioId)
                ))
                .collect(Collectors.toList());
    }
}
