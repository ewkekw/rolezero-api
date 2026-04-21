package com.role0.core.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.role0.adapter.out.persistence.entity.SolicitacaoParticipacaoJpaEntity;
import com.role0.adapter.out.persistence.repository.SpringDataSolicitacaoRepository;
import com.role0.core.application.port.out.ChatNotificationPort;
import com.role0.core.application.port.out.EventoRepositoryPort;
import com.role0.core.application.port.out.UsuarioRepositoryPort;
import com.role0.core.application.port.out.MessageBrokerEventPort;
import com.role0.core.application.usecase.ProcessarSolicitacaoUseCase;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.evento.service.GatilhoSocialService;
import com.role0.core.domain.evento.valueobject.StatusEvento;
import com.role0.core.domain.evento.valueobject.StatusSolicitacao;
import com.role0.core.domain.usuario.entity.Usuario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessarSolicitacaoService implements ProcessarSolicitacaoUseCase {

    private final EventoRepositoryPort eventoRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final ChatNotificationPort chatNotification;
    private final MessageBrokerEventPort messageBroker;
    private final GatilhoSocialService gatilhoSocialService;
    private final SpringDataSolicitacaoRepository solicitacaoRepository;

    public ProcessarSolicitacaoService(
            EventoRepositoryPort eventoRepository,
            UsuarioRepositoryPort usuarioRepository,
            ChatNotificationPort chatNotification,
            MessageBrokerEventPort messageBroker,
            GatilhoSocialService gatilhoSocialService,
            SpringDataSolicitacaoRepository solicitacaoRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.chatNotification = chatNotification;
        this.messageBroker = messageBroker;
        this.gatilhoSocialService = gatilhoSocialService;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @Override
    @Transactional
    public void aprovar(UUID eventoId, UUID hostId, UUID solicitacaoId) {
        SolicitacaoParticipacaoJpaEntity solicitacao = solicitacaoRepository.findById(solicitacaoId)
            .orElseThrow(() -> new EventoDomainException("Solicitação não encontrada"));

        UUID participanteId = solicitacao.getUsuarioId();

        Evento evento = eventoRepository.buscarPorId(eventoId)
            .orElseThrow(() -> new EventoDomainException("Evento inexistente"));

        if (!evento.getHostId().equals(hostId)) {
            throw new EventoDomainException("Apenas o anfitrião (host) pode aprovar solicitações.");
        }

        evento.aprovarParticipante(participanteId);
        eventoRepository.salvar(evento);

        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        solicitacaoRepository.save(solicitacao);

        chatNotification.notificarNovoParticipante(eventoId, participanteId);

        if (evento.getStatus() == StatusEvento.FECHADO_PREGAME) {
            tratarRotinaGrupoFechado(evento);
        }
    }

    private void tratarRotinaGrupoFechado(Evento evento) {
        List<Usuario> usuarios = evento.getParticipantesAprovados().stream()
            .map(id -> usuarioRepository.buscarPorId(id).orElse(null))
            .filter(u -> u != null)
            .collect(Collectors.toList());

        String iceBreaker = gatilhoSocialService.gerarIceBreaker(evento, usuarios);
        chatNotification.enviarGatilhoDeConversa(evento.getId(), iceBreaker);

        // Agenda nativamente na fila RabbitMQ o encerramento do evento para a expiração automática dos logs e status 
        // 24 horas a partir desse momento (Delay Exchange)
        messageBroker.agendarEncerramentoDeEvento(evento.getId(), 24);
    }

    @Override
    public void recusar(UUID eventoId, UUID hostId, UUID solicitacaoId) {
        // Operação opaca por motivos de produto para não gerar Notificação Reativa negativa. Apenas descarta do DB.
    }
}
