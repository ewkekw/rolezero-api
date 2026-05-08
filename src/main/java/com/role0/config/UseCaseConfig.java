package com.role0.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.GeocodingServicePort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.service.CriarEventoService;
import com.role0.core.application.usecase.CriarEventoUseCase;
import com.role0.core.domain.evento.service.GatilhoSocialService;

/**
 * Inversão de Controle Manual para classes do core que não têm @Service.
 * Services com @Service são detectados automaticamente pelo component scan.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CriarEventoUseCase criarEventoUseCase(
            EventoRepositoryPort eventoRepository,
            UsuarioRepositoryPort usuarioRepository,
            GeocodingServicePort geocodingService) {
        return new CriarEventoService(eventoRepository, usuarioRepository, geocodingService);
    }

    @Bean
    public GatilhoSocialService gatilhoSocialService() {
        return new GatilhoSocialService();
    }
}
