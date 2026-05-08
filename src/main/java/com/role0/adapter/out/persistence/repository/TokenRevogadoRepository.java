package com.role0.adapter.out.persistence.repository;

import com.role0.adapter.out.persistence.entity.TokenRevogadoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRevogadoRepository extends JpaRepository<TokenRevogadoJpaEntity, String> {
}
