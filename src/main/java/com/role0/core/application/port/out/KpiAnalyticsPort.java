package com.role0.core.application.port.out;

import java.util.UUID;

/**
 * Skill: Data Engineer
 * Porta de saída passiva. Utilizada para disparar eventos assíncronos de Analytics (KPIs)
 * para um Data Lake ou Data Warehouse (ex: via Kafka/Kinesis) sem onerar a transação ACID do RDS.
 */
public interface KpiAnalyticsPort {
    void publicarTaxaComparecimento(UUID eventoId, int checkIns, int totalAprovados);
    void publicarRecorrenciaUsuario(UUID usuarioId, int totalEventosParticipados);
}
