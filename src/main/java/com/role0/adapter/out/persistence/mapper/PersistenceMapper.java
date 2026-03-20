package com.role0.adapter.out.persistence.mapper;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.role0.adapter.out.persistence.entity.EventoJpaEntity;
import com.role0.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.role0.core.domain.evento.entity.Evento;
import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;
import com.role0.core.domain.usuario.entity.Usuario;

@Mapper(componentModel = "spring")
public abstract class PersistenceMapper {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // -- Evento --

    @Mapping(target = "localizacao", source = "localizacao", qualifiedByName = "coordenadaToPoint")
    public abstract EventoJpaEntity toJpaEntity(Evento dominio);

    @Mapping(target = "localizacao", source = "localizacao", qualifiedByName = "pointToCoordenada")
    public abstract Evento toDomain(EventoJpaEntity entity);

    // -- Usuario --

    @Mapping(target = "nome", source = "nomeDisplay")
    @Mapping(target = "tags", source = "vibeTags")
    @Mapping(target = "provedIdentityToken", source = "biometriaValidada")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    public abstract UsuarioJpaEntity toJpaEntity(Usuario dominio);

    @Mapping(target = "nomeDisplay", source = "nome")
    @Mapping(target = "vibeTags", source = "tags")
    @Mapping(target = "biometriaValidada", source = "provedIdentityToken")
    @Mapping(target = "tokenVerificacaoBiometrica", ignore = true)
    @Mapping(target = "trustScore", ignore = true) // Trust score is usually calculated or in separate table
    public abstract Usuario toDomain(UsuarioJpaEntity entity);

    // -- Spatial Converters (WGS84) --

    @Named("coordenadaToPoint")
    protected Point coordenadaToPoint(CoordenadaGeografica loc) {
        if (loc == null)
            return null;
        // JTS receives Longitude (X), Latitude (Y)
        return geometryFactory.createPoint(new Coordinate(loc.getLongitude(), loc.getLatitude()));
    }

    @Named("pointToCoordenada")
    protected CoordenadaGeografica pointToCoordenada(Point point) {
        if (point == null)
            return null;
        // Extracts Latitude (Y), Longitude (X)
        return new CoordenadaGeografica(point.getY(), point.getX());
    }
}
