package com.role0.core.domain.usuario.entity;

import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import com.role0.core.domain.usuario.valueobject.VibeTag;
import com.role0.core.domain.usuario.exception.UsuarioDomainException;

public class Usuario {

    private final UUID id;
    private String nomeDisplay;
    private double trustScore;
    private Set<VibeTag> vibeTags;
    private boolean biometriaValidada;
    private String tokenVerificacaoBiometrica;

    public Usuario(UUID id, String nomeDisplay) {
        if (id == null)
            throw new UsuarioDomainException("ID do usuário é obrigatório");
        if (nomeDisplay == null || nomeDisplay.isBlank())
            throw new UsuarioDomainException("Nome do usuário é obrigatório");
        this.id = id;
        this.nomeDisplay = nomeDisplay;
        this.vibeTags = new HashSet<>();
        this.biometriaValidada = false;
    }

    public void mudarNome(String novoNome) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new UsuarioDomainException("Novo nome de exibição não pode ser vazio.");
        }
        this.nomeDisplay = novoNome.trim();
    }

    public void adicionarVibeTag(VibeTag tag) {
        if (tag == null)
            return;
        if (this.vibeTags.size() >= 5) {
            throw new UsuarioDomainException("O limite máximo de 5 Vibe Tags foi atingido.");
        }
        this.vibeTags.add(tag);
    }

    public Set<VibeTag> getVibeTags() {
        return Collections.unmodifiableSet(this.vibeTags);
    }

    public void setVibeTags(Set<VibeTag> vibeTags) {
        this.vibeTags = vibeTags != null ? new HashSet<>(vibeTags) : new HashSet<>();
    }

    public void validarBiometria(String tokenFornecedorIdV) {
        if (tokenFornecedorIdV == null || tokenFornecedorIdV.isBlank()) {
            throw new UsuarioDomainException("Token biométrico inválido.");
        }
        this.tokenVerificacaoBiometrica = tokenFornecedorIdV;
        this.biometriaValidada = true;
    }

    public boolean isBiometriaValidada() {
        return biometriaValidada;
    }

    public void setBiometriaValidada(boolean biometriaValidada) {
        this.biometriaValidada = biometriaValidada;
    }

    public void setTokenVerificacaoBiometrica(String tokenVerificacaoBiometrica) {
        this.tokenVerificacaoBiometrica = tokenVerificacaoBiometrica;
    }

    public String getTokenVerificacaoBiometrica() {
        return tokenVerificacaoBiometrica;
    }

    public UUID getId() {
        return id;
    }

    public String getNomeDisplay() {
        return nomeDisplay;
    }

    public double getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(double trustScore) {
        this.trustScore = trustScore;
    }
}
