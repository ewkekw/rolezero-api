package com.role0.core.application.dto;

import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.usuario.entity.Usuario;
import com.role0.core.application.port.out.WeatherServicePort.PrevisaoClima;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

public record EventoDetalheOutput(
    UUID id,
    String titulo,
    String descricao,
    String status,
    int capacidadeMaxima,
    int totalAprovados,
    LocalDateTime inicioEm,
    String enderecoLegivel,
    HostInfo host,
    Optional<PrevisaoClima> clima
) {
    public record HostInfo(UUID id, String nomeDisplay, double trustScore) {}

    public static EventoDetalheOutput from(Evento evento, int totalAprovados, Usuario host, Optional<PrevisaoClima> clima) {
        return new EventoDetalheOutput(
            evento.getId(),
            evento.getTitulo(),
            evento.getDescricao(),
            evento.getStatus().name(),
            evento.getCapacidadeMaxima(),
            totalAprovados,
            evento.getHorarioInicio(),
            evento.getEnderecoLegivel(),
            new HostInfo(host.getId(), host.getNomeDisplay(), host.getTrustScore()),
            clima
        );
    }
}
