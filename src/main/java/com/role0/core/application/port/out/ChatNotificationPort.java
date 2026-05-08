package com.role0.core.application.port.out;

import java.util.UUID;

public interface ChatNotificationPort {
    void notificarNovoParticipante(UUID eventoId, UUID participanteId);
    void enviarGatilhoDeConversa(UUID eventoId, String mensagemGatilho);
    void congelarChatLegalmente(UUID eventoId); // Retém logs para autoridade após "Pânico"
}
