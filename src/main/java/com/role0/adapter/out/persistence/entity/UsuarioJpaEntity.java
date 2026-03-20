package com.role0.adapter.out.persistence.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import com.role0.core.domain.usuario.valueobject.VibeTag;

@Entity
@Table(name = "role_usuario")
public class UsuarioJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nome;

    @ElementCollection(targetClass = VibeTag.class)
    @CollectionTable(name = "usuario_vibe_tags", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private List<VibeTag> tags;

    @Column(name = "biometria_validada", nullable = false)
    private boolean provedIdentityToken;

    @Column(name = "password_hash")
    private String passwordHash;

    // Getters / Setters omitted for brevity but they are necessary for Hibernate
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<VibeTag> getTags() {
        return tags;
    }

    public void setTags(List<VibeTag> tags) {
        this.tags = tags;
    }

    public boolean isProvedIdentityToken() {
        return provedIdentityToken;
    }

    public void setProvedIdentityToken(boolean provedIdentityToken) {
        this.provedIdentityToken = provedIdentityToken;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
