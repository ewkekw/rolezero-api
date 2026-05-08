package com.role0.adapter.out.persistence.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    
    java.util.Optional<UsuarioJpaEntity> findByEmail(String email);

    @Query(value = "SELECT u.* FROM role_usuario u JOIN role_perfil_reputacao r ON u.id = r.usuario_id WHERE u.id IN :ids ORDER BY r.trust_score DESC LIMIT 1", nativeQuery = true)
    java.util.Optional<UsuarioJpaEntity> findBestSubstituteIn(@Param("ids") List<UUID> ids);
}
