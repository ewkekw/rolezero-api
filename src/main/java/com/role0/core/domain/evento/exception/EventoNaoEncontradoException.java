package com.role0.core.domain.evento.exception;

public class EventoNaoEncontradoException extends EventoDomainException {
    public EventoNaoEncontradoException(String eventId) {
        super("Nenhum evento com o ID '" + eventId + "' foi encontrado.");
    }
}
