package com.role0.core.application.service;

import com.role0.core.application.dto.EventoDetalheOutput;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.port.out.WeatherServicePort;
import com.role0.core.application.port.out.WeatherServicePort.PrevisaoClima;
import com.role0.core.application.usecase.BuscarEventoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoNaoEncontradoException;
import com.role0.core.domain.usuario.entity.Usuario;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BuscarEventoService implements BuscarEventoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final WeatherServicePort weatherServicePort;

    public BuscarEventoService(EventoRepositoryPort eventoRepository, 
                               UsuarioRepositoryPort usuarioRepository,
                               WeatherServicePort weatherServicePort) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.weatherServicePort = weatherServicePort;
    }

    @Override
    public EventoDetalheOutput executar(UUID eventId) {
        Evento evento = eventoRepository.buscarPorId(eventId)
                .orElseThrow(() -> new EventoNaoEncontradoException(eventId.toString()));

        // O host do evento deve existir se a integridade relacional estiver correta
        Usuario host = usuarioRepository.buscarPorId(evento.getHostId())
                .orElseThrow(() -> new RuntimeException("Host do evento não encontrado. Inconsistência de dados."));

        // Conta quantos participantes estão aprovados (usaremos o lazy loading do port ou métrica isolada)
        // No momento a entidade Evento possui os IDs ou a consulta no repository retorna.
        // Simularemos um método de contagem no repository para ser performático.
        int totalAprovados = eventoRepository.contarParticipantesAprovados(eventId);

        // Busca a previsão de tempo baseada nas coordenadas (lon, lat via obj geometrial ou via getLocalizacao)
        Optional<PrevisaoClima> clima = weatherServicePort.getPrevisaoPorCoordenadas(
                evento.getLocalizacao().getLatitude(), 
                evento.getLocalizacao().getLongitude()  
        );

        return EventoDetalheOutput.from(evento, totalAprovados, host, clima);
    }
}
