package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload de login com e-mail e senha.")
public record LoginRequest(
    @Schema(description = "E-mail cadastrado", example = "joao@role0.com")
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    String email,

    @Schema(description = "Senha do usuário", example = "Senha@2025")
    @NotBlank(message = "A senha é obrigatória.")
    String senha
) {}
