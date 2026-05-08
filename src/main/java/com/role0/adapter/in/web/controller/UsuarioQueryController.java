package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.response.UsuarioPerfilResponse;
import com.role0.adapter.in.web.dto.response.UsuarioPublicoResponse;
import com.role0.core.application.usecase.BuscarPerfilPublicoUseCase;
import com.role0.core.application.usecase.BuscarPerfilUsuarioUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "4. Usuários e Perfil", description = "Query e Mutation dos dados dos Participantes.")
public class UsuarioQueryController {

    private final BuscarPerfilUsuarioUseCase buscarPerfilUsuarioUseCase;
    private final BuscarPerfilPublicoUseCase buscarPerfilPublicoUseCase;

    public UsuarioQueryController(BuscarPerfilUsuarioUseCase buscarPerfilUsuarioUseCase, BuscarPerfilPublicoUseCase buscarPerfilPublicoUseCase) {
        this.buscarPerfilUsuarioUseCase = buscarPerfilUsuarioUseCase;
        this.buscarPerfilPublicoUseCase = buscarPerfilPublicoUseCase;
    }

    @Operation(summary = "Meu Perfil Completo", description = "Retorna os dados sensíveis do próprio usuário autenticado, incluindo e-mail.")
    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilResponse> buscarMeuPerfil() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(401).build();
        }
        UUID usuarioAutenticadoId = (UUID) principal;
        UsuarioPerfilResponse response = buscarPerfilUsuarioUseCase.executar(usuarioAutenticadoId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Perfil Público", description = "Retorna os dados públicos (mascarados) de um usuário específico, sem revelar informações sensíveis como email.")
    @GetMapping("/{userId}")
    public ResponseEntity<UsuarioPublicoResponse> buscarPerfilPublico(@org.springframework.lang.NonNull @org.springframework.web.bind.annotation.PathVariable UUID userId) {
        UsuarioPublicoResponse response = buscarPerfilPublicoUseCase.executar(userId);
        return ResponseEntity.ok(response);
    }
}
