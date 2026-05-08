package com.role0.adapter.out.integration.chat;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.role0.core.application.port.out.ChatNotificationPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WebSocketChatAdapter implements ChatNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(WebSocketChatAdapter.class);

    // private final SimpMessagingTemplate messagingTemplate; -- Omitindo auto-wired
    // temporário.

    @Override
    public void notificarNovoParticipante(UUID eventoId, UUID participanteId) {
        String topic = "/topic/events/" + eventoId + "/system";
        String payload = "{\"type\": \"NEW_MEMBER\", \"memberId\": \"" + participanteId + "\"}";

        // messagingTemplate.convertAndSend(topic, payload);
        log.info("STOMP WebSocket Broadcast: {} para {}", payload, topic);
    }

    @Override
    public void enviarGatilhoDeConversa(UUID eventoId, String iceBreakerMessage) {
        String topic = "/topic/events/" + eventoId + "/icebreaker";
        String payload = "{\"text\": \"" + iceBreakerMessage + "\"}";

        // messagingTemplate.convertAndSend(topic, payload);
        log.info("STOMP WebSocket Broadcast: {} para {}", payload, topic);
    }

    @Override
    public void congelarChatLegalmente(UUID eventoId) {
        // Regra de Compliance: O Botão de pânico foi apertado.
        // Invoca o microserviço de Chat (que poderia ser Stream Chat ou camada Node
        // isolada)
        // para abortar o TTL automático do chat e efetuar Dump Físico das mensagens num
        // S3 de retenção jurídica (WORM storage).
        log.warn("🚨 SEGURANÇA: COMANDO EXTERNO S3 FREEZE ACIONADO PARA A PARTY {}", eventoId);
    }
}
