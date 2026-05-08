package com.role0.adapter.out.integration.analytics;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.role0.core.application.port.out.KpiAnalyticsPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class KpiPublisherAdapter implements KpiAnalyticsPort {

    private static final Logger log = LoggerFactory.getLogger(KpiPublisherAdapter.class);

    // private final KafkaTemplate kafkaTemplate; // Omitido p/ mock

    @Override
    public void publicarTaxaComparecimento(UUID eventoId, int checkIns, int totalAprovados) {
        String kpiPayload = String.format("{\"eventoId\":\"%s\", \"checkIns\":%d, \"totalAprovados\":%d}", 
            eventoId, checkIns, totalAprovados);
            
        // kafkaTemplate.send("analytics.kpi.comparecimento", kpiPayload);
        log.info("📊 DATA ENGINEER [Kafka/Firehose]: Analytics passivo 'Taxa de Comparecimento' emitido p/ Lakehouse: {}", kpiPayload);
    }

    @Override
    public void publicarRecorrenciaUsuario(UUID usuarioId, int totalEventosParticipados) {
        String kpiPayload = String.format("{\"usuarioId\":\"%s\", \"frequenciaAcumulada\":%d}", 
            usuarioId, totalEventosParticipados);
            
        // kafkaTemplate.send("analytics.kpi.recorrencia", kpiPayload);
        log.info("📊 DATA ENGINEER [Kafka/Firehose]: Analytics passivo 'Taxa de Recorrência' emitido p/ Lakehouse: {}", kpiPayload);
    }
}
