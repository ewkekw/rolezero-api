package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload público de Sign-In para troca e validação de Identity Tokens.")
public record SsoLoginRequest(
    @Schema(description = "JWT puramente assinado pelo Backend do Autenticador (Apple/Google)", example = "eyJhbGciOiJSUzI1NiIsImtp...21A")
    @NotBlank(message = "O token do provedor SSO é obrigatório.")
    String providerToken,
    
    @NotBlank(message = "O nome do provedor é obrigatório.")
    String providerName
) {}
