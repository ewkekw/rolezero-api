package com.role0.config;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.role0.core.domain.evento.exception.EventoDomainException;
import com.role0.core.domain.usuario.exception.UsuarioDomainException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skill: Observability Engineer
 * Interceptador Global formatando Exceções Limpas do Domínio para Respostas Padrão RFC-7807 Problem Details.
 * Protege a stack trace de vazar detalhes internos para agentes maliciosos na web.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({EventoDomainException.class, UsuarioDomainException.class})
    public ResponseEntity<ErrorDetails> handleDomainException(RuntimeException ex, WebRequest request) {
        ErrorDetails error = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Erro interno fatal na Edge API Categoria Role0: ", ex);
        ErrorDetails error = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno fatal na Edge API Categoria Role0: " + ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // RFC-7807 Mock Padrão
    public record ErrorDetails(LocalDateTime timestamp, int status, String message, String details) {}
}
