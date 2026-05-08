package com.role0.core.domain.reputacao.valueobject;

public class TrustScore {
    private final double value;
    private final int avaliacoesTotais;

    public TrustScore(double value, int avaliacoesTotais) {
        if (value < 0.0 || value > 5.0) {
            throw new IllegalArgumentException("Score deve estar entre 0.0 e 5.0.");
        }
        if (avaliacoesTotais < 0) {
            throw new IllegalArgumentException("Avaliações totais não pode ser negativo.");
        }
        this.value = value;
        this.avaliacoesTotais = avaliacoesTotais;
    }

    public double getValue() {
        return value;
    }

    public int getAvaliacoesTotais() {
        return avaliacoesTotais;
    }

    public TrustScore adicionarAvaliacao(double novaNota) {
        if (novaNota < 0.0 || novaNota > 5.0) {
            throw new IllegalArgumentException("A nova nota deve estar entre 0.0 e 5.0.");
        }

        double somaAtual = this.value * this.avaliacoesTotais;
        double novaSoma = somaAtual + novaNota;
        int novoTotal = this.avaliacoesTotais + 1;
        double novoValor = novaSoma / novoTotal;

        return new TrustScore(novoValor, novoTotal);
    }
}
