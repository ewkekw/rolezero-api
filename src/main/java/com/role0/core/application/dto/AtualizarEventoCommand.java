package com.role0.core.application.dto;

public record AtualizarEventoCommand(
    String titulo,
    String descricao,
    Integer maxCapacity
) {}
