package com.role0.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.role0.adapter.out.persistence.entity.AvaliacaoJpaEntity;

@Repository
public interface SpringDataAvaliacaoRepository extends JpaRepository<AvaliacaoJpaEntity, UUID> {

    List<AvaliacaoJpaEntity> findByAvaliadoIdOrderByCreatedAtDesc(UUID avaliadoId, Pageable pageable);

    long countByAvaliadoId(UUID avaliadoId);
}
