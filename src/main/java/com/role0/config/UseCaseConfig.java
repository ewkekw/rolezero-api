package com.role0.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.role0.core.application.port.out.ChatNotificationPort;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.GeocodingServicePort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.port.out.MessageBrokerEventPort;
import com.role0.core.application.service.CriarEventoService;
import com.role0.core.domain.evento.service.GatilhoSocialService;
import com.role0.core.application.usecase.CriarEventoUseCase;
import com.role0.core.application.usecase.AcionarBotaoPanicoUseCase;
import com.role0.core.application.usecase.EncerrarEventoUseCase;
import com.role0.core.application.usecase.ProcessarSolicitacaoUseCase;
import com.role0.core.application.usecase.RealizarCheckInUseCase;
import com.role0.core.application.usecase.SolicitarParticipacaoUseCase;
import com.role0.core.application.service.AcionarBotaoPanicoService;
import com.role0.core.application.service.EncerrarEventoService;
import com.role0.core.application.service.ProcessarSolicitacaoService;
import com.role0.core.application.service.RealizarCheckInService;
import com.role0.core.application.service.SolicitarParticipacaoService;

/**
 * Skill: Senior Architect
 * Inversão de Controle Manual. O Domínio e o Application Core são módulos puros
 * de Java (Hexagonal).
 * É responsabilidade do Spring (Adapter In/Config) instanciá-los e injetar os
 * Adapters Reais
 * implementando nas Outbound Ports.
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
    public EncerrarEventoUseCase encerrarEventoUseCase(EventoRepositoryPort eventoRepository) {
        return new EncerrarEventoService(eventoRepository);
    }

    @Bean
    public GatilhoSocialService gatilhoSocialService() {
        return new GatilhoSocialService();
    }

    @Bean
    public ProcessarSolicitacaoUseCase processarSolicitacaoUseCase(
            EventoRepositoryPort eventoRepository,
            UsuarioRepositoryPort usuarioRepository,
            ChatNotificationPort chatNotification,
            MessageBrokerEventPort messageBroker,
            GatilhoSocialService gatilhoSocialService) {
        return new ProcessarSolicitacaoService(eventoRepository, usuarioRepository, chatNotification, messageBroker,
                gatilhoSocialService);
    }

    @Bean
    public RealizarCheckInUseCase realizarCheckInUseCase(EventoRepositoryPort eventoRepository) {
        return new RealizarCheckInService(eventoRepository);
    }

    @Bean
    public AcionarBotaoPanicoUseCase acionarBotaoPanicoUseCase(
            EventoRepositoryPort eventoRepository,
            ChatNotificationPort chatNotification) {
        return new AcionarBotaoPanicoService(eventoRepository, chatNotification);
    }

    @Bean
    public SolicitarParticipacaoUseCase solicitarParticipacaoUseCase(EventoRepositoryPort eventoRepository) {
        return new SolicitarParticipacaoService(eventoRepository);
    }
}
