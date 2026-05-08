package com.role0.adapter.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import com.role0.core.domain.evento.valueobject.StatusEvento;

@Entity
@Table(name = "role_evento")
public class EventoJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "host_id", nullable = false)
    private UUID hostId;

    @Column(name = "capacidade_maxima", nullable = false)
    private int capacidadeMaxima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status;

    @Column(name = "horario_inicio", nullable = false)
    private LocalDateTime horarioInicio;

    // PostGIS Geometry Point explicitly declared (SRID 4326 for WGS84 GPS coords)
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point localizacao;

    @ElementCollection
    @CollectionTable(name = "evento_participantes", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "participante_id")
    private List<UUID> participantesAprovados;

    @Column(name = "incidente_reportado", nullable = false)
    private boolean incidenteReportado = false;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "endereco_legivel")
    private String enderecoLegivel;

    // Getters e Setters p/ Hibernate...
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public UUID getHostId() {
        return hostId;
    }

    public void setHostId(UUID hostId) {
        this.hostId = hostId;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public void setStatus(StatusEvento status) {
        this.status = status;
    }

    public LocalDateTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalDateTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public Point getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Point localizacao) {
        this.localizacao = localizacao;
    }

    public List<UUID> getParticipantesAprovados() {
        return participantesAprovados;
    }

    public void setParticipantesAprovados(List<UUID> participantesAprovados) {
        this.participantesAprovados = participantesAprovados;
    }

    public boolean isIncidenteReportado() {
        return incidenteReportado;
    }

    public void setIncidenteReportado(boolean incidenteReportado) {
        this.incidenteReportado = incidenteReportado;
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
}
