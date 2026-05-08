package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload para confirmar transação presencial baseada no dispositivo móvel do Hóspede.")
public record CheckInRequest(
    @Schema(description = "A coordenada Latitude lida do Hardware do participante no exato milisegundo de CheckIn", example = "-23.550500")
    @NotNull(message = "A latitude atual do participante é obrigatória.")
    @Min(-90) @Max(90)
    Double latitude,

    @Schema(description = "A coordenada Longitude lida do Hardware do participante no exato milisegundo de CheckIn", example = "-46.633310")
    @NotNull(message = "A longitude atual do participante é obrigatória.")
    @Min(-180) @Max(180)
    Double longitude
) {}
