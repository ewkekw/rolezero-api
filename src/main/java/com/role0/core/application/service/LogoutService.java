package com.role0.core.application.service;

import org.springframework.stereotype.Service;

import com.role0.core.application.port.out.TokenBlacklistPort;
import com.role0.core.application.usecase.LogoutUseCase;

@Service
public class LogoutService implements LogoutUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;

    public LogoutService(TokenBlacklistPort tokenBlacklistPort) {
        this.tokenBlacklistPort = tokenBlacklistPort;
    }

    @Override
    public void executar(String tokenRaw) {
        if (tokenRaw != null && !tokenRaw.isBlank()) {
            tokenBlacklistPort.revogarToken(tokenRaw);
        }
    }
}
