package com.role0.adapter.out.persistence;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.role0.adapter.out.persistence.entity.TokenRevogadoJpaEntity;
import com.role0.adapter.out.persistence.repository.TokenRevogadoRepository;
import com.role0.core.application.port.out.TokenBlacklistPort;

@Component
public class TokenBlacklistAdapter implements TokenBlacklistPort {

    private final TokenRevogadoRepository repository;

    public TokenBlacklistAdapter(TokenRevogadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void revogarToken(@NonNull String tokenRaw) {
        if (tokenRaw.isBlank()) return;
        String hash = hashToken(tokenRaw);
        if (!repository.existsById(hash)) {
            repository.save(new TokenRevogadoJpaEntity(hash, LocalDateTime.now()));
        }
    }

    @Override
    public boolean isRevogado(@NonNull String tokenRaw) {
        if (tokenRaw.isBlank()) return false;
        String hash = hashToken(tokenRaw);
        return repository.existsById(hash);
    }

    @NonNull
    @SuppressWarnings("null")
    private String hashToken(@NonNull String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash do token", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
