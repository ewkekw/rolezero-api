package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payload para criação de um novo Rolê privativo.
 * Conforme documentação técnica v1.0 — seção 5.2 (POST /events).
 */
@Schema(description = "Payload para criação de um novo Rolê privativo.")
public record CriarEventoRequest(

    @Schema(description = "Título chamativo do evento", example = "Resenha do Japa")
    @NotBlank(message = "O título do rolê é obrigatório.")
    @Size(min = 3, max = 120, message = "O título deve ter entre 3 e 120 caracteres.")
    String titulo,

    @Schema(description = "Descrição livre do evento (opcional)", example = "Rolê tranquilo pra jogar e tomar uma gelada.")
    @Size(max = 500, message = "A descrição não pode ultrapassar 500 caracteres.")
    String descricao,

    @Schema(description = "Número máximo de participantes", example = "5")
    @Min(value = 2, message = "O evento precisa ter pelo menos 2 vagas.")
    @Max(value = 50, message = "O limite atual do Role-Zero é de 50 vagas.")
    int capacidadeMaxima,

    @Schema(description = "Latitude exata do local presencial", example = "-23.550520")
    @NotNull(message = "A latitude é obrigatória.")
    @Min(-90) @Max(90)
    Double latitude,

    @Schema(description = "Longitude exata do local presencial", example = "-46.633308")
    @NotNull(message = "A longitude é obrigatória.")
    @Min(-180) @Max(180)
    Double longitude,

    @Schema(description = "Horário de início programado", example = "2026-12-31T22:00:00")
    @NotNull(message = "O horário de início é obrigatório.")
    @Future(message = "O rolê deve ser planejado para o futuro.")
    LocalDateTime horarioInicio,

    @Schema(description = "Tags de interesse do evento (máx 5)", example = "[\"CRAFT_BEER\", \"BOARDGAMES\"]")
    @Size(max = 5, message = "Máximo de 5 VibeTags por evento.")
    List<String> vibeTags

) {}
