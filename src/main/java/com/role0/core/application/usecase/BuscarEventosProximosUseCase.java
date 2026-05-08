package com.role0.core.application.usecase;

import com.role0.adapter.in.web.dto.response.EventoCardResponse;
import com.role0.core.domain.usuario.valueobject.VibeTag;

import java.util.List;

/**
 * Use Case de busca de eventos próximos ao usuário.
 * Executa a varredura geoespacial via PostGIS e retorna os cards formatados.
 */
public interface BuscarEventosProximosUseCase {
    List<EventoCardResponse> executar(double lat, double lon, double raioKm, List<VibeTag> tagsFilter);
}
