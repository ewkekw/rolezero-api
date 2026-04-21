package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.request.AtualizarPerfilRequest;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.usecase.AtualizarPerfilUseCase;
import com.role0.core.domain.usuario.entity.Usuario;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "4. Usuários e Perfil")
public class UsuarioAcaoController {

    private final AtualizarPerfilUseCase atualizarPerfilUseCase;
    private final UsuarioRepositoryPort usuarioRepository;

    public UsuarioAcaoController(AtualizarPerfilUseCase atualizarPerfilUseCase, UsuarioRepositoryPort usuarioRepository) {
        this.atualizarPerfilUseCase = atualizarPerfilUseCase;
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(summary = "Atualizar Meu Perfil", description = "Permite que o usuário autenticado atualize informações mutáveis básicas do seu próprio perfil.")
    @PatchMapping("/me")
    public ResponseEntity<Void> atualizarMeuPerfil(@Valid @RequestBody AtualizarPerfilRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID usuarioAutenticadoId = (UUID) principal;

        atualizarPerfilUseCase.executar(usuarioAutenticadoId, request.nomeDisplay(), request.vibeTags());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Validar Identidade (Biometria)", description = "Ativa a validação biométrica do usuário. Necessária para criar eventos.")
    @PostMapping("/me/biometria")
    public ResponseEntity<Void> validarBiometria() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID usuarioId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));
        usuario.validarBiometria("TOKEN_VALIDADO_" + usuarioId);
        usuarioRepository.salvar(usuario);
        return ResponseEntity.noContent().build();
    }
}
