package com.role0.core.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.role0.adapter.in.web.dto.response.MensagemChatResponse;
import com.role0.adapter.out.persistence.entity.MensagemChatJpaEntity;
import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataMensagemChatRepository;
import com.role0.adapter.out.persistence.repository.SpringDataUsuarioRepository;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.ListarSolicitacoesQueryPort;
import com.role0.core.application.usecase.BuscarHistoricoChatUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoNaoEncontradoException;

import org.springframework.data.domain.PageRequest;

@Service
public class BuscarHistoricoChatService implements BuscarHistoricoChatUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final ListarSolicitacoesQueryPort listQueryPort;
    private final SpringDataMensagemChatRepository chatRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    public BuscarHistoricoChatService(
            EventoRepositoryPort eventoRepository,
            ListarSolicitacoesQueryPort listQueryPort,
            SpringDataMensagemChatRepository chatRepository,
            SpringDataUsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.listQueryPort = listQueryPort;
        this.chatRepository = chatRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @SuppressWarnings("null")
    public List<MensagemChatResponse> executar(@NonNull UUID eventoId, @NonNull UUID solicitanteId, int limit) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new EventoNaoEncontradoException(eventoId.toString()));

        boolean isHost = evento.getHostId().equals(solicitanteId);
        boolean isParticipante = listQueryPort.listarAprovadosPorEvento(eventoId).stream()
                .anyMatch(s -> s.getUsuarioId().equals(solicitanteId));

        if (!isHost && !isParticipante) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Apenas participantes aprovados e o anfitrião podem ler o chat.");
        }

        int pageSize = Math.min(limit, 100); // cap at 100
        List<MensagemChatJpaEntity> mensagens = chatRepository
                .findByEventoIdOrderByTimestampEnvioDesc(eventoId, PageRequest.of(0, pageSize));

        return mensagens.stream()
                .map(m -> new MensagemChatResponse(
                        m.getId(),
                        m.getSenderId(),
                        usuarioRepository.findById(m.getSenderId())
                                .map(UsuarioJpaEntity::getNome).orElse("Usuário"),
                        m.getConteudo(),
                        m.getTimestampEnvio(),
                        m.getTipo()
                ))
                .collect(Collectors.toList());
    }
}
