package com.role0.core.application.port.out;

import java.util.Optional;

/**
 * Outbound Port para resolução de endereços via Geocoding Reverso.
 * O domínio e os serviços de aplicação dependem desta interface — nunca
 * da implementação concreta (NominatimClient, Google Maps, etc.).
 */
public interface GeocodingServicePort {

    /**
     * Recebe coordenadas geográficas (latitude, longitude) e retorna um endereço
     * legível em português para exibição ao usuário.
     *
     * @param lat latitude WGS84
     * @param lon longitude WGS84
     * @return endereço formatado (ex: "Rua Augusta, Consolação, São Paulo")
     *         ou Optional.empty() se o serviço externo estiver indisponível (fallback tolerante).
     */
    Optional<String> resolverEnderecoPorCoordenadas(double lat, double lon);
}
