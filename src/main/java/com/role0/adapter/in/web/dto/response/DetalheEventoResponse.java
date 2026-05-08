package com.role0.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import com.role0.core.domain.evento.valueobject.StatusEvento;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO contendo todos os detalhes renderizados na tela principal do Evento, exibido apenas para convidados aprovados ou Host.")
public record DetalheEventoResponse(
    @Schema(description = "UUID interno do Evento")
    UUID id,
    String titulo,
    int capacidadeMaxima,
    int quantidadeAprovados,
    StatusEvento status,
    LocalDateTime horarioInicio,
    UUID hostId,
    List<UUID> participantesIds // Omitindo PII sensível dos participantes pra a página pública
) {}
