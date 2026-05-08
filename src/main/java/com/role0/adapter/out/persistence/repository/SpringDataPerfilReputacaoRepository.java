package com.role0.adapter.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.role0.adapter.out.persistence.entity.PerfilReputacaoJpaEntity;

@Repository
public interface SpringDataPerfilReputacaoRepository extends JpaRepository<PerfilReputacaoJpaEntity, UUID> {

    Optional<PerfilReputacaoJpaEntity> findByUsuarioId(UUID usuarioId);

    boolean existsByUsuarioId(UUID usuarioId);
}
