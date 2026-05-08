package com.role0.core.domain.reputacao;

import com.role0.core.domain.reputacao.entity.PerfilReputacao;
import com.role0.core.domain.reputacao.valueobject.TrustScore;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReputacaoTest {

    @Test
    void naoDevePermitirTrustScoreInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new TrustScore(-1.0, 0));
        assertThrows(IllegalArgumentException.class, () -> new TrustScore(5.1, 0));
    }

    @Test
    void deveCalcularNovaMediaAoReceberAvaliacoes() {
        PerfilReputacao perfil = new PerfilReputacao(UUID.randomUUID());
        
        // Starts with (5.0 * 0 + 4.0) / 1 = 4.0
        perfil.processarNovaAvaliacao(4.0);
        assertEquals(4.0, perfil.getScoreAtual().getValue(), 0.01);
        assertEquals(1, perfil.getScoreAtual().getAvaliacoesTotais());

        // Now: (4.0 * 1 + 2.0) / 2 = 3.0
        perfil.processarNovaAvaliacao(2.0);
        assertEquals(3.0, perfil.getScoreAtual().getValue(), 0.01);
        assertEquals(2, perfil.getScoreAtual().getAvaliacoesTotais());
    }
}
