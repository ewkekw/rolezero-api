package com.role0.config.security;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Serviço JWT real usando JJWT (io.jsonwebtoken 0.12.x).
 * - Algoritmo: HS256 com chave derivada do JWT_SECRET configurado em application.yml
 * - Claims: sub = UUID do usuário, iat = emissão, exp = expiração
 * - Stateless: nenhum estado em memória — apenas parseamento da assinatura
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(JwtProperties jwtProperties) {
        this.signingKey = Keys.hmacShaKeyFor(
            jwtProperties.getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        this.expirationMs = jwtProperties.getExpirationMs();
    }

    /**
     * Gera um JWT assinado com HS256 contendo o UUID do usuário no claim 'sub'.
     */
    public String generateToken(UUID usuarioId) {
        long nowMs = System.currentTimeMillis();
        return Jwts.builder()
                .subject(usuarioId.toString())
                .issuedAt(new Date(nowMs))
                .expiration(new Date(nowMs + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Valida a assinatura e verifica se o token não está expirado.
     * Retorna false em vez de lançar exceção para encadeamento seguro no filtro.
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrai o UUID do usuário do claim 'sub'.
     * Deve ser chamado apenas após validar o token com isTokenValid().
     */
    @org.springframework.lang.NonNull
    public String extractUserId(String token) {
        String subject = parseClaims(token).getSubject();
        if (subject == null) {
            throw new JwtException("Token não contém o claim 'sub'");
        }
        return subject;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
