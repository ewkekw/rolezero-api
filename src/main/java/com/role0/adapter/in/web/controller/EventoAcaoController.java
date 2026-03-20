package com.role0.adapter.in.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.role0.adapter.in.web.dto.request.CheckInRequest;
import com.role0.adapter.in.web.dto.request.CriarEventoRequest;
import com.role0.adapter.in.web.dto.request.AtualizarEventoRequest;
import com.role0.core.application.usecase.AcionarBotaoPanicoUseCase;
import com.role0.core.application.usecase.AtualizarEventoUseCase;
import com.role0.core.application.usecase.CancelarEventoUseCase;
import com.role0.core.application.usecase.CriarEventoUseCase;
import com.role0.core.application.usecase.RealizarCheckInUseCase;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.application.dto.AtualizarEventoCommand;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller focado puramente nas Ações Mutacionais do Evento.
 * Não realiza queries, não gera DTOs massivos, apenas orquestra intenções à
 * camada Application.
 */
@RestController
@RequestMapping("/api/v1/events")
@Validated
@Tag(name = "3. Interações com Eventos", description = "Comandos transacionais (Criar, CheckIn e SOS) sobre Eventos.")
public class EventoAcaoController {

    private final CriarEventoUseCase criarEventoUseCase;
    private final RealizarCheckInUseCase realizarCheckInUseCase;
    private final AcionarBotaoPanicoUseCase acionarBotaoPanicoUseCase;
    private final AtualizarEventoUseCase atualizarEventoUseCase;
    private final CancelarEventoUseCase cancelarEventoUseCase;

    public EventoAcaoController(
            CriarEventoUseCase criarEventoUseCase,
            RealizarCheckInUseCase realizarCheckInUseCase,
            AcionarBotaoPanicoUseCase acionarBotaoPanicoUseCase,
            AtualizarEventoUseCase atualizarEventoUseCase,
            CancelarEventoUseCase cancelarEventoUseCase) {
        this.criarEventoUseCase = criarEventoUseCase;
        this.realizarCheckInUseCase = realizarCheckInUseCase;
        this.acionarBotaoPanicoUseCase = acionarBotaoPanicoUseCase;
        this.atualizarEventoUseCase = atualizarEventoUseCase;
        this.cancelarEventoUseCase = cancelarEventoUseCase;
    }

    @Operation(summary = "Criar Evento Novo", description = "Host cria um evento restrito. Necessita Bearer Token.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso com Location formatado"),
            @ApiResponse(responseCode = "400", description = "Validation Fail no DTO")
    })
    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody CriarEventoRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID hostIdAutenticado = (UUID) principal;

        CoordenadaGeografica localizacao = new CoordenadaGeografica(request.latitude(), request.longitude());
        criarEventoUseCase.executar(
                hostIdAutenticado,
                request.titulo(),
                request.capacidadeMaxima(),
                localizacao,
                request.horarioInicio());

        // Retorna HTTP 201 Created apontando para URI do novo recurso (Location header
        // logic omitida por brevidade)
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Finalizar CheckIn Definitivo", description = "Disponível apenas para participantes já aprovados e que cruzem o raio de 50m físico via Geometria Euclidiana do Domínio.")
    @PostMapping("/{id}/check-in")
    public ResponseEntity<Void> efetuarCheckIn(
            @Parameter(description = "UUID do Evento", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id, 
            @Valid @RequestBody CheckInRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userAutenticado = (UUID) principal;
        realizarCheckInUseCase.executar(id, userAutenticado,
                new CoordenadaGeografica(request.latitude(), request.longitude()));
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint crítico para segurança física de usuários.
     * Acionado durante um evento para sinalizar ao backend retenção forense de logs
     * da party (conforme ADR-001).
     */
    @Operation(summary = "🔥 Botão de Pânico (SOS)", description = "Acorta Ciclos da Fila. Ativa Congelamento Forense da Sala (WORM AWS S3). Tratamento cego e tolerante a falhas (202 Accepted sempre) para não vazar pânico a terceiros.")
    @PostMapping("/{id}/panic")
    public ResponseEntity<Void> acionarEmergencia(
            @Parameter(description = "UUID do Evento reportado", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID reportadorAutenticado = (UUID) principal;

        acionarBotaoPanicoUseCase.executar(id, reportadorAutenticado);

        // A API responde de forma otimista/minimalista para não gerar latência ou erros
        // obvios diante do agressor
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Editar Evento", description = "Host edita título, descrição e/ou capacidade do evento.")
    @org.springframework.web.bind.annotation.PatchMapping("/{id}")
    public ResponseEntity<Void> atualizarEvento(
            @Parameter(description = "UUID do Evento", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id,
            @Valid @RequestBody AtualizarEventoRequest request) {
        
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userAutenticado = (UUID) principal;

        AtualizarEventoCommand command = new AtualizarEventoCommand(
                request.titulo(),
                request.descricao(),
                request.maxCapacity()
        );

        atualizarEventoUseCase.executar(id, command, userAutenticado);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancelar Evento", description = "Host cancela o evento. Pode transferir a liderança (Interino) se faltar menos de 2h.")
    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarEvento(
            @Parameter(description = "UUID do Evento a ser cancelado", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UUID)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID hostIdAutenticado = (UUID) principal;

        cancelarEventoUseCase.executar(id, hostIdAutenticado);

        return ResponseEntity.noContent().build();
    }
}
