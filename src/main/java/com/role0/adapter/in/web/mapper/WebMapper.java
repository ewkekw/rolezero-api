package com.role0.adapter.in.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.role0.adapter.in.web.dto.response.DetalheEventoResponse;
import com.role0.core.domain.evento.entity.Evento;

@Mapper(componentModel = "spring")
public interface WebMapper {

    @Mapping(target = "quantidadeAprovados", expression = "java(evento.getParticipantesAprovados().size())")
    @Mapping(target = "participantesIds", source = "participantesAprovados")
    DetalheEventoResponse toDetalheResponse(Evento evento);

    // O EventoCardResponse necessita do cálculo cruzado de GPS que é feito na
    // camada de Repository Adapter
    // e entregue no Evento com o transient `distancia` populado, mas o core mapping
    // base fica aqui.
}
