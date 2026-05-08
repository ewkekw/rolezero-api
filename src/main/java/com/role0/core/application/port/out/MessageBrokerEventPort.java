package com.role0.core.application.port.out;

import java.util.UUID;

public interface MessageBrokerEventPort {
    void agendarEncerramentoDeEvento(UUID eventoId, long delayEmHoras);
}
