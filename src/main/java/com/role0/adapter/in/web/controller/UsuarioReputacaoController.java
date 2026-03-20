package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.request.AvaliarUsuarioRequest;
import com.role0.adapter.in.web.dto.response.PerfilReviewsResponse;
import com.role0.core.application.usecase.AvaliarUsuarioUseCase;
import com.role0.core.application.usecase.BuscarAvaliacoesUsuarioUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "6. Reputação", description = "Avaliações e Trust Score entre usuários após o evento.")
public class UsuarioReputacaoController {

    private final BuscarAvaliacoesUsuarioUseCase buscarAvaliacoesUseCase;
    private final AvaliarUsuarioUseCase avaliarUsuarioUseCase;

    public UsuarioReputacaoController(
            BuscarAvaliacoesUsuarioUseCase buscarAvaliacoesUseCase,
            AvaliarUsuarioUseCase avaliarUsuarioUseCase) {
        this.buscarAvaliacoesUseCase = buscarAvaliacoesUseCase;
        this.avaliarUsuarioUseCase = avaliarUsuarioUseCase;
    }

    @Operation(
        summary = "Listar Avaliações Recebidas",
        description = "Retorna o TrustScore e as últimas avaliações recebidas por um usuário. Público."
    )
    @GetMapping("/{userId}/reviews")
    public ResponseEntity<PerfilReviewsResponse> listarAvaliacoes(
            @Parameter(description = "UUID do Usuário") @PathVariable UUID userId,
            @RequestParam(name = "limit", defaultValue = "20") int limit) {
        @SuppressWarnings("null")
        PerfilReviewsResponse response = buscarAvaliacoesUseCase.executar(userId, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Avaliar Usuário",
        description = "Submete uma avaliação (1-5) para outro usuário após um evento compartilhado. Recalcula o TrustScore imediatamente."
    )
    @PostMapping("/{userId}/reviews")
    public ResponseEntity<Void> avaliarUsuario(
            @Parameter(description = "UUID do Usuário a ser avaliado") @PathVariable UUID userId,
            @Valid @RequestBody AvaliarUsuarioRequest payload) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID avaliadorId = (UUID) principal;
        avaliarUsuarioUseCase.executar(avaliadorId, userId, payload);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
