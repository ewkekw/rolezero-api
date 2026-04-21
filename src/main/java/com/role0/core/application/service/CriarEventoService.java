package com.role0.core.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.GeocodingServicePort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.usecase.CriarEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.usuario.entity.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CriarEventoService implements CriarEventoUseCase {

    private static final Logger log = LoggerFactory.getLogger(CriarEventoService.class);

    private final EventoRepositoryPort eventoRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final GeocodingServicePort geocodingService;

    public CriarEventoService(
            EventoRepositoryPort eventoRepository,
            UsuarioRepositoryPort usuarioRepository,
            GeocodingServicePort geocodingService) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.geocodingService = geocodingService;
    }

    @Override
    public Evento executar(UUID hostId, String titulo, int capacidadeMaxima, CoordenadaGeografica localizacao,
            LocalDateTime horarioInicio) {
        // Validação: Host deve existir e ter biometria validada (Zero-Knowledge)
        Usuario host = usuarioRepository.buscarPorId(hostId)
                .orElseThrow(() -> new EventoDomainException("Host não encontrado"));

        if (!host.isBiometriaValidada()) {
            throw new EventoDomainException("A validação biométrica com liveness é obrigatória para criar um rolê.");
        }

        Evento evento = new Evento(UUID.randomUUID(), host.getId(), titulo, capacidadeMaxima, localizacao,
                horarioInicio);

        evento.setStatus(com.role0.core.domain.evento.valueobject.StatusEvento.ABERTO_PARA_VAGAS);

        // Host é automaticamente o primeiro aprovado no seu próprio evento
        evento.aprovarParticipante(host.getId());

        // Enriquecimento com endereço legível via Nominatim (geocoding reverso).
        // Operação tolerante a falhas — se o serviço externo estiver indisponível,
        // o evento é salvo normalmente com enderecoLegivel = null.
        geocodingService.resolverEnderecoPorCoordenadas(
                localizacao.getLatitude(),
                localizacao.getLongitude()
        ).ifPresentOrElse(
                evento::setEnderecoLegivel,
                () -> log.warn("Geocoding indisponível para evento '{}'. Endereço salvo como null.", titulo)
        );

        return eventoRepository.salvar(evento);
    }
}
