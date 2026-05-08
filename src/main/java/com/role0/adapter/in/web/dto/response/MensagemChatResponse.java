package com.role0.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record MensagemChatResponse(
    UUID id,
    UUID senderId,
    String senderNome,
    String conteudo,
    LocalDateTime timestampEnvio,
    String tipo
) {}
