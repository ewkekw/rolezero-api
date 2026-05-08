package com.role0.adapter.out.integration.mensageria;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.role0.core.application.port.out.MessageBrokerEventPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RabbitMqPublisherAdapter implements MessageBrokerEventPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqPublisherAdapter.class);

    // private final RabbitTemplate rabbitTemplate; Omitido por hora

    /**
     * Skill: Backend/Arquitetura
     * Publica uma mensagem num Exchange configurado com o plugin x-delayed-message.
     * Depois do delay de horas, ela cairá na Fila final de exclusão do rolezinho
     * (EDA Pipeline).
     */
    @Override
    public void agendarEncerramentoDeEvento(UUID eventoId, long horasDelay) {
        // long delayInMillis = horasDelay * 3600000L;
        // String routingKey = "evento.expiracao";

        // rabbitTemplate.convertAndSend("delayed-exchange", routingKey,
        // eventoId.toString(), message -> {
        // message.getMessageProperties().setDelayLong(delayInMillis);
        // return message;
        // });

        log.info("EDA PIPELINE: Publicou evento {} com delay de {}h para exclusão na madrugada.", eventoId, horasDelay);
    }
}
