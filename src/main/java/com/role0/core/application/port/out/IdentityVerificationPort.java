package com.role0.core.application.port.out;

public interface IdentityVerificationPort {
    /**
     * Validação biométrica Zero-Knowledge.
     * Envia os bytes/stream da foto para API terceira e retorna apenas o Token de validação de liveness.
     * A foto NUNCA deve ser gravada por nós.
     */
    String extrairTokenBiometricoValido(byte[] imageBytes); // Returns the token
}
