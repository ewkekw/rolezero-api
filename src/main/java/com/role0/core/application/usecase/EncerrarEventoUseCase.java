package com.role0.core.application.usecase;

import java.util.UUID;

/**
 * Ponto de entrada chamado assincronamente pelo RabbitMqConsumer após 24h
 * do inicio do evento.
 */
public interface EncerrarEventoUseCase {
    void executar(UUID eventoId);
}
