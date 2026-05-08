package com.role0.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta contendo o Access Token assinado pela API (Formato JWT Bearer).")
public record TokenResponse(
    @Schema(description = "Token Bearer Stateless para injetar no Header Authorization", example = "eyJhb...Xyz")
    String token,
    long expiresIn
) {}
