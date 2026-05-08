package com.role0.core.domain.estabelecimento.entity;

import java.util.UUID;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;

public class Estabelecimento {
    private final UUID id;
    private String nome;
    private CoordenadaGeografica localizacao;
    private int cotasDisponiveisRoleZero; // Mesas oferecidas para Hosts

    public Estabelecimento(UUID id, String nome, CoordenadaGeografica localizacao, int cotasDisponiveisRoleZero) {
        if (id == null) throw new IllegalArgumentException("ID do estabelecimento é obrigatório.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");
        if (localizacao == null) throw new IllegalArgumentException("Localização é obrigatória.");
        
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.cotasDisponiveisRoleZero = cotasDisponiveisRoleZero;
    }

    public void consumirCota() {
        if (this.cotasDisponiveisRoleZero <= 0) {
            throw new IllegalStateException("Cotas esgotadas para este estabelecimento.");
        }
        this.cotasDisponiveisRoleZero--;
    }

    public void adicionarCotas(int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva.");
        this.cotasDisponiveisRoleZero += quantidade;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public CoordenadaGeografica getLocalizacao() { return localizacao; }
    public int getCotasDisponiveisRoleZero() { return cotasDisponiveisRoleZero; }
}
