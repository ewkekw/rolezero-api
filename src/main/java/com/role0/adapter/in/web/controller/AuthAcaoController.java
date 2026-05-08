package com.role0.adapter.in.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.core.application.usecase.LogoutUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints de gerenciamento de sessão e tokens")
public class AuthAcaoController {

    private final LogoutUseCase logoutUseCase;

    public AuthAcaoController(LogoutUseCase logoutUseCase) {
        this.logoutUseCase = logoutUseCase;
    }

    @Operation(summary = "Logout (Revogar Token)", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/session")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutUseCase.executar(token);
        }
        return ResponseEntity.noContent().build();
    }
}
