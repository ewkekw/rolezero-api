package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload para criação de conta no Role0.")
public record RegisterRequest(
    @Schema(description = "Nome de exibição do usuário", example = "João das Neves")
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 100)
    String nome,

    @Schema(description = "E-mail único do usuário", example = "joao@role0.com")
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    String email,

    @Schema(description = "Senha (mínimo 8 caracteres)", example = "Senha@2025")
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "A senha precisa ter no mínimo 8 caracteres.")
    String senha
) {}
