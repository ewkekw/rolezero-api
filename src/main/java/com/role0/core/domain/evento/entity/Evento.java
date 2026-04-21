package com.role0.core.domain.evento.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.evento.valueobject.StatusEvento;

public class Evento {
    private final UUID id;
    private UUID hostId;
    private String titulo;
    private int capacidadeMaxima;
    private StatusEvento status;
    private CoordenadaGeografica localizacao;
    private LocalDateTime horarioInicio;
    private String descricao;
    private String enderecoLegivel;
    private boolean incidenteReportado;

    private final List<UUID> participantesAprovados;

    public Evento(UUID id, UUID hostId, String titulo, int capacidadeMaxima, CoordenadaGeografica localizacao,
            LocalDateTime horarioInicio) {
        if (id == null || hostId == null)
            throw new EventoDomainException("ID do evento e do host são obrigatórios");
        if (capacidadeMaxima <= 1)
            throw new EventoDomainException("O evento precisa de pelo menos 2 vagas");

        this.id = id;
        this.hostId = hostId;
        this.titulo = titulo;
        this.capacidadeMaxima = capacidadeMaxima;
        this.localizacao = localizacao;
        this.horarioInicio = horarioInicio;
        this.status = StatusEvento.CRIADO;
        this.incidenteReportado = false;
        this.participantesAprovados = new ArrayList<>();
    }

    public void setStatus(StatusEvento status) {
        this.status = status;
    }

    public void aprovarParticipante(UUID participanteId) {
        if (this.status != StatusEvento.ABERTO_PARA_VAGAS) {
            throw new EventoDomainException("O evento não está aceitando novos participantes.");
        }
        if (this.participantesAprovados.size() >= this.capacidadeMaxima) {
            throw new EventoDomainException("Capacidade máxima do evento atingida.");
        }
        this.participantesAprovados.add(participanteId);

        if (this.participantesAprovados.size() == this.capacidadeMaxima) {
            this.status = StatusEvento.FECHADO_PREGAME;
        }
    }

    public void atualizarDetalhes(String titulo, String descricao, Integer maxCapacity, int totalAprovadosAtual) {
        if (maxCapacity != null) {
            if (maxCapacity < totalAprovadosAtual) {
                throw new EventoDomainException("A nova capacidade máxima não pode ser menor que o número atual de aprovados.");
            }
            if (maxCapacity <= 1) {
                throw new EventoDomainException("O evento precisa de pelo menos 2 vagas.");
            }
            this.capacidadeMaxima = maxCapacity;
        }
        if (titulo != null && !titulo.isBlank()) {
            this.titulo = titulo;
        }
        if (descricao != null && !descricao.isBlank()) {
            this.descricao = descricao;
        }
    }

    // Raio de 50 metros
    public boolean validarProximidadeCheckIn(CoordenadaGeografica userLocation) {
        double distMeters = this.localizacao.calcularDistanciaMetros(userLocation);
        return distMeters <= 50.0;
    }

    // Contingência de Cancelamento
    // Regra: Se faltar < 2h para o inicio, e houver um participante mais confiável
    // (promovidoInterinoId),
    // o evento sobrevive trocando de dono. Se não, o evento morre.
    public void cancelarPeloHost(UUID requestHostId, UUID promovidoInterinoId, LocalDateTime agora) {
        if (!this.hostId.equals(requestHostId)) {
            throw new EventoDomainException("Apenas o host atual pode cancelar o evento");
        }

        long horasParaInicio = ChronoUnit.HOURS.between(agora, this.horarioInicio);

        if (horasParaInicio < 2 && promovidoInterinoId != null
                && this.participantesAprovados.contains(promovidoInterinoId)) {
            // Continência ativada: Salva a noite do grupo
            this.hostId = promovidoInterinoId;
            this.participantesAprovados.remove(promovidoInterinoId); // Ele deixa de ser 'participante' para virar
                                                                     // 'host'
        } else {
            // Cancela definitivamente
            this.status = StatusEvento.EXPIRADO;
        }
    }

    public void reportarIncidente() {
        this.incidenteReportado = true;
    }

    public boolean isIncidenteReportado() {
        return incidenteReportado;
    }

    public UUID getId() {
        return id;
    }

    public UUID getHostId() {
        return hostId;
    }

    public String getTitulo() {
        return titulo;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public List<UUID> getParticipantesAprovados() {
        return Collections.unmodifiableList(participantesAprovados);
    }

    public void setParticipantesAprovados(List<UUID> ids) {
        this.participantesAprovados.clear();
        if (ids != null) {
            this.participantesAprovados.addAll(ids);
        }
    }

    public CoordenadaGeografica getLocalizacao() {
        return localizacao;
    }

    public LocalDateTime getHorarioInicio() {
        return horarioInicio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEnderecoLegivel() {
        return enderecoLegivel;
    }

    public void setEnderecoLegivel(String enderecoLegivel) {
        this.enderecoLegivel = enderecoLegivel;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }
}
