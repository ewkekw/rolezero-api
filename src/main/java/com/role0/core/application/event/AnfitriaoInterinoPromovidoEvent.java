package com.role0.core.application.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AnfitriaoInterinoPromovidoEvent(
    UUID eventoId,
    UUID antigoHostId,
    UUID novoHostId,
    LocalDateTime ocorridoEm
) {}
