package com.role0.core.application.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.role0.core.application.port.out.TokenBlacklistPort;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenBlacklistPort tokenBlacklistPort;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void deveRevogarTokenValido() {
        String token = "ey.token.valido";
        logoutService.executar(token);
        verify(tokenBlacklistPort).revogarToken(token);
    }

    @Test
    void naoDeveRevogarTokenVazioOUNulo() {
        logoutService.executar(null);
        logoutService.executar("");
        logoutService.executar("   ");
        
        verifyNoInteractions(tokenBlacklistPort);
    }
}
