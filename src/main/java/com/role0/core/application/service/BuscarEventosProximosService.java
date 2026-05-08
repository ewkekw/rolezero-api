package com.role0.core.application.service;

import com.role0.adapter.in.web.dto.response.EventoCardResponse;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.usecase.BuscarEventosProximosUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.usuario.entity.Usuario;
import com.role0.core.domain.usuario.valueobject.VibeTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de busca geoespacial de eventos próximos.
 *
 * Orquestra a consulta ao PostGIS via EventoRepositoryPort, busca o usuário host
 * de cada evento para montar o card com informações de TrustScore e nome,
 * e calcula a distância em metros usando o Value Object CoordenadaGeografica (Haversine).
 */
@Service
public class BuscarEventosProximosService implements BuscarEventosProximosUseCase {

    private static final Logger log = LoggerFactory.getLogger(BuscarEventosProximosService.class);

    private final EventoRepositoryPort eventoRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    public BuscarEventosProximosService(EventoRepositoryPort eventoRepository, UsuarioRepositoryPort usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<EventoCardResponse> executar(double lat, double lon, double raioKm, List<VibeTag> tagsFilter) {
        CoordenadaGeografica localizacaoUsuario = new CoordenadaGeografica(lat, lon);

        List<Evento> eventosProximos = eventoRepository.buscarEventosProximos(localizacaoUsuario, raioKm, tagsFilter);

        log.info("Busca geoespacial: {} eventos encontrados no raio de {}km para lat={}, lon={}", 
                eventosProximos.size(), raioKm, lat, lon);

        return eventosProximos.stream()
                .map(evento -> mapToCard(evento, localizacaoUsuario))
                .toList();
    }

    private EventoCardResponse mapToCard(Evento evento, CoordenadaGeografica localizacaoUsuario) {
        // Busca o host para obter nome e trust score — campos exigidos pelo contrato da API
        Optional<Usuario> hostOpt = usuarioRepository.buscarPorId(evento.getHostId());
        String nomeHost = hostOpt.map(Usuario::getNomeDisplay).orElse("Anfitrião");
        double trustScoreHost = hostOpt.map(Usuario::getTrustScore).orElse(0.0);

        // Calcula distância real: CoordenadaGeografica.calcularDistanciaMetros usa Haversine
        double distanciaEmMetros = evento.getLocalizacao()
                .calcularDistanciaMetros(localizacaoUsuario);

        // Formata a ocupação como "atual/máximo" — ex: "3/5"
        int aprovados = evento.getParticipantesAprovados().size();
        String ocupacao = aprovados + "/" + evento.getCapacidadeMaxima();
        int vagasRestantes = evento.getCapacidadeMaxima() - aprovados;

        return new EventoCardResponse(
                evento.getId(),
                evento.getTitulo(),
                nomeHost,
                trustScoreHost,
                vagasRestantes,
                ocupacao,
                evento.getStatus(),
                evento.getHorarioInicio(),
                distanciaEmMetros,
                evento.getEnderecoLegivel()
        );
    }
}
