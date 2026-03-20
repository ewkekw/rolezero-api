package com.role0.adapter.in.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.response.EventoCardResponse;
import com.role0.adapter.in.web.dto.response.MensagemChatResponse;
import com.role0.adapter.in.web.dto.response.MeuEventoResponse;
import com.role0.core.application.dto.EventoDetalheOutput;
import com.role0.core.application.usecase.BuscarEventoUseCase;
import com.role0.core.application.usecase.BuscarEventosProximosUseCase;
import com.role0.core.application.usecase.BuscarHistoricoChatUseCase;
import com.role0.core.application.usecase.ListarMeusEventosUseCase;
import com.role0.core.domain.usuario.valueobject.VibeTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "2. Busca e Indexação Geográfica", description = "Radar de Eventos. Rate Limiting ativo por IP para prevenir Web Scraping.")
public class EventoBuscaController {

    private static final Logger log = LoggerFactory.getLogger(EventoBuscaController.class);

    private final BuscarEventoUseCase buscarEventoUseCase;
    private final BuscarEventosProximosUseCase buscarEventosProximosUseCase;
    private final ListarMeusEventosUseCase listarMeusEventosUseCase;
    private final BuscarHistoricoChatUseCase buscarHistoricoChatUseCase;

    public EventoBuscaController(
            BuscarEventoUseCase buscarEventoUseCase,
            BuscarEventosProximosUseCase buscarEventosProximosUseCase,
            ListarMeusEventosUseCase listarMeusEventosUseCase,
            BuscarHistoricoChatUseCase buscarHistoricoChatUseCase) {
        this.buscarEventoUseCase = buscarEventoUseCase;
        this.buscarEventosProximosUseCase = buscarEventosProximosUseCase;
        this.listarMeusEventosUseCase = listarMeusEventosUseCase;
        this.buscarHistoricoChatUseCase = buscarHistoricoChatUseCase;
    }

    @Operation(summary = "Radar de Eventos Abertos", description = "Aciona o PostGIS Spatial Query ST_DWithin para recuperar os Eventos abertos baseados no Raio e GPS do Usuário.")
    @GetMapping("/nearby")
    public ResponseEntity<List<EventoCardResponse>> buscarProximos(
            @Parameter(description = "Latitude exata do usuário", required = true) @RequestParam(name = "latitude") double latitude,
            @Parameter(description = "Longitude exata do usuário", required = true) @RequestParam(name = "longitude") double longitude,
            @Parameter(description = "Raio de dispersão em Quilômetros (Default: 10km)", required = false) @RequestParam(name = "raioKm", defaultValue = "10.0") double raioKm,
            @Parameter(description = "Filtro por VibeTags (opcional). Ex: CRAFT_BEER,GAMES") @RequestParam(name = "vibeTags", required = false) List<String> vibeTagStrings) {

        // Converte strings para domain VibeTags, ignorando silenciosamente valores inválidos
        List<VibeTag> tagsFilter = vibeTagStrings == null ? List.of() : vibeTagStrings.stream()
                .map(tag -> {
                    try {
                        return VibeTag.valueOf(tag.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        log.warn("VibeTag desconhecida ignorada no filtro: '{}'", tag);
                        return null;
                    }
                })
                .filter(tag -> tag != null)
                .toList();

        return ResponseEntity.ok(buscarEventosProximosUseCase.executar(latitude, longitude, raioKm, tagsFilter));
    }

    @Operation(summary = "Detalhes Ricos do Evento", description = "Recupera os detalhes completos do evento, incluindo contagem de vagas, informações do anfitrião e clima no local.")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventoDetalheOutput> buscarDetalheEvento(
            @Parameter(description = "ID do Evento em formato UUID", required = true) @PathVariable UUID eventId) {
        return ResponseEntity.ok(buscarEventoUseCase.executar(eventId));
    }

    @Operation(summary = "Meus Eventos", description = "Lista todos os eventos que o usuário autenticado é anfitrião ou participante aprovado, ordenados pelos mais recentes.")
    @GetMapping("/my")
    public ResponseEntity<List<MeuEventoResponse>> buscarMeusEventos() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(401).build();
        }
        UUID usuarioAutenticadoId = (UUID) principal;
        return ResponseEntity.ok(listarMeusEventosUseCase.executar(usuarioAutenticadoId));
    }

    @Operation(summary = "Histórico do Chat do Evento", description = "Retorna as últimas N mensagens do chat. Apenas participantes aprovados e o anfitrião têm acesso.")
    @GetMapping("/{eventId}/chat/history")
    public ResponseEntity<List<MensagemChatResponse>> buscarHistoricoChat(
            @Parameter(description = "UUID do Evento") @PathVariable UUID eventId,
            @RequestParam(name = "limit", defaultValue = "50") int limit) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(401).build();
        }
        UUID solicitanteId = (UUID) principal;
        @SuppressWarnings("null")
        var response = buscarHistoricoChatUseCase.executar(eventId, solicitanteId, limit);
        return ResponseEntity.ok(response);
    }
}
