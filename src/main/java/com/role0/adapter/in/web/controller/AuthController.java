package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.role0.adapter.in.web.dto.request.LoginRequest;
import com.role0.adapter.in.web.dto.request.RegisterRequest;
import com.role0.adapter.in.web.dto.response.TokenResponse;
import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataUsuarioRepository;
import com.role0.config.security.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "1. Autenticação", description = "Endpoints públicos de cadastro e login. Nenhuma autenticação exigida aqui.")
public class AuthController {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(SpringDataUsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Cadastrar usuário", description = "Cria uma nova conta no Role0 com e-mail e senha. Retorna um JWT válido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Conta criada e token emitido"),
        @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já está em uso.");
        }

        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setNome(request.nome());
        entity.setEmail(request.email());
        entity.setPasswordHash(passwordEncoder.encode(request.senha()));
        entity.setProvedIdentityToken(false);

        @SuppressWarnings("null")
        UsuarioJpaEntity saved = usuarioRepository.save(entity);

        String token = jwtService.generateToken(saved.getId());
        long expiresInSeconds = 86400; // 24h
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(token, expiresInSeconds));
    }

    @Operation(summary = "Login", description = "Autentica com e-mail e senha. Retorna um JWT Bearer válido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido e token emitido"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        UsuarioJpaEntity usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas."));

        if (usuario.getPasswordHash() == null || 
            !passwordEncoder.matches(request.senha(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        String token = jwtService.generateToken(usuario.getId());
        long expiresInSeconds = 86400;
        return ResponseEntity.ok(new TokenResponse(token, expiresInSeconds));
    }
}
