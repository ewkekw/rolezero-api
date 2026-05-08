package com.role0.core.application.port.out;

import org.springframework.lang.NonNull;

public interface TokenBlacklistPort {
    void revogarToken(@NonNull String tokenRaw);
    boolean isRevogado(@NonNull String tokenRaw);
}
