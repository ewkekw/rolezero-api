package com.role0.core.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.domain.usuario.entity.Usuario;
import com.role0.core.domain.usuario.exception.UsuarioDomainException;

@ExtendWith(MockitoExtension.class)
class AtualizarPerfilServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks
    private AtualizarPerfilService atualizarPerfilService;

    @Test
    void deveAtualizarNomeComSucesso() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario(id, "Antigo Nome");

        when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));

        atualizarPerfilService.executar(id, "Novo Nome Trimb");

        assertEquals("Novo Nome Trimb", usuario.getNomeDisplay());
        verify(usuarioRepository).salvar(usuario);
    }

    @Test
    void deveEstourarErroQuandoNomeVazio() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario(id, "Antigo Nome");

        when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.of(usuario));

        UsuarioDomainException ex = assertThrows(UsuarioDomainException.class, () -> {
            atualizarPerfilService.executar(id, "   ");
        });
        org.junit.jupiter.api.Assertions.assertNotNull(ex);
    }

    @Test
    void deveEstourarErroQuandoUsuarioNaoExiste() {
        UUID id = UUID.randomUUID();

        when(usuarioRepository.buscarPorId(id)).thenReturn(Optional.empty());

        UsuarioDomainException ex = assertThrows(UsuarioDomainException.class, () -> {
            atualizarPerfilService.executar(id, "Valido");
        });
        org.junit.jupiter.api.Assertions.assertNotNull(ex);
    }
}
