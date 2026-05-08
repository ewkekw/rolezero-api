package com.role0.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.role0.adapter.out.persistence.entity.EventoJpaEntity;

@Repository
public interface SpringDataEventoRepository extends JpaRepository<EventoJpaEntity, UUID> {

    /**
     * Skill: Postgres Best Practices / Database Architect
     * Utiliza-se ST_DWithin projetado em modo geography porque os dados em
     * EPSG:4326 exigem este casting nativo
     * para efetuar cálculos perfeitos sobre o globo refrator em metros e usar
     * Índices Espaciais (GIST) nativos sem varredura em massa (Seq Scan).
     */
    @Query(value = """
            SELECT e.* FROM role_evento e
            WHERE e.status = :status
            AND ST_DWithin(CAST(e.localizacao AS geography), CAST(:pontoUsuario AS geography), :raioEmMetros)
            """, nativeQuery = true)
    List<EventoJpaEntity> findNearbyEventos(
            @Param("pontoUsuario") Point pontoUsuario,
            @Param("raioEmMetros") double raioEmMetros,
            @Param("status") String status);

    @Query(value = "SELECT count(*) FROM evento_participantes WHERE evento_id = :eventoId", nativeQuery = true)
    int countParticipantesAprovados(@Param("eventoId") UUID eventoId);

    @Query("""
            SELECT e FROM EventoJpaEntity e
            WHERE (e.hostId = :usuarioId)
               OR (:usuarioId MEMBER OF e.participantesAprovados)
            ORDER BY e.horarioInicio DESC
            """)
    List<EventoJpaEntity> findMeusEventos(@Param("usuarioId") UUID usuarioId);
}
