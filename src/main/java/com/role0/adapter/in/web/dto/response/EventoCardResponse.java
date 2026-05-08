package com.role0.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.role0.core.domain.evento.valueobject.StatusEvento;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Card de evento exibido na tela principal de descoberta do aplicativo.
 * Representa um evento próximo ao usuário com informações essenciais para decisão.
 *
 * Conforme documentação técnica v1.0 — seção 5.2.
 */
@Schema(description = "Resumo de um evento para exibição no radar geográfico.")
public record EventoCardResponse(
    @Schema(description = "ID único do evento", example = "a1b2c3d4-...")
    UUID id,

    @Schema(description = "Título do rolê", example = "Cerveja Artesanal e Boardgames")
    String titulo,

    @Schema(description = "Nome do host do evento", example = "Matheus")
    String nomeHost,

    @Schema(description = "Trust Score do host (0.0 a 5.0)", example = "4.8")
    double trustScoreHost,

    @Schema(description = "Vagas disponíveis no evento", example = "2")
    int vagasRestantes,

    @Schema(description = "Ocupação formatada (atual/máximo)", example = "3/5")
    String ocupacao,

    @Schema(description = "Status atual do evento")
    StatusEvento status,

    @Schema(description = "Horário de início", example = "2025-03-20T20:00:00")
    LocalDateTime horarioInicio,

    @Schema(description = "Distância do usuário em metros", example = "1540")
    double distanciaEmMetros,

    @Schema(description = "Endereço legível resolvido pelo Nominatim", example = "Rua Augusta, Consolação, São Paulo")
    String enderecoLegivel
) {}
