package com.role0.adapter.in.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record AtualizarEventoRequest(
    @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
    String titulo,

    @Size(max = 500, message = "A descrição não pode passar de 500 caracteres")
    String descricao,

    @Min(value = 2, message = "O evento precisa suportar pelo menos 2 pessoas (você e mais um)")
    Integer maxCapacity
) {}
