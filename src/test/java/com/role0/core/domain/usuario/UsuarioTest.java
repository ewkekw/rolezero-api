package com.role0.core.domain.usuario;

import com.role0.core.domain.usuario.entity.Usuario;
import com.role0.core.domain.usuario.valueobject.VibeTag;
import com.role0.core.domain.usuario.exception.UsuarioDomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void deveAdicionarVibeTagComSucesso() {
        Usuario usuario = new Usuario(UUID.randomUUID(), "Fulano");
        
        usuario.adicionarVibeTag(VibeTag.INDIE_MUSIC);
        usuario.adicionarVibeTag(VibeTag.BOARD_GAMES);
        
        assertEquals(2, usuario.getVibeTags().size());
        assertTrue(usuario.getVibeTags().contains(VibeTag.INDIE_MUSIC));
    }
    
    @Test
    void naoDevePermitirMaisDeCincoVibeTags() {
        Usuario usuario = new Usuario(UUID.randomUUID(), "Fulano");
        
        usuario.adicionarVibeTag(VibeTag.INDIE_MUSIC);
        usuario.adicionarVibeTag(VibeTag.BOARD_GAMES);
        usuario.adicionarVibeTag(VibeTag.CRAFT_BEER);
        usuario.adicionarVibeTag(VibeTag.TECH_TALKS);
        usuario.adicionarVibeTag(VibeTag.SPORTS);
        
        UsuarioDomainException exception = assertThrows(
            UsuarioDomainException.class,
            () -> usuario.adicionarVibeTag(VibeTag.CAFE)
        );
        assertEquals("O limite máximo de 5 Vibe Tags foi atingido.", exception.getMessage());
    }
    
    @Test
    void deveValidarBiometriaRetendoApenasTokenZeroKnowledge() {
        Usuario usuario = new Usuario(UUID.randomUUID(), "Fulano");
        String fakeToken = "zk_token_39485";
        
        usuario.validarBiometria(fakeToken);
        
        assertTrue(usuario.isBiometriaValidada());
        assertEquals(fakeToken, usuario.getTokenVerificacaoBiometrica());
    }
}
