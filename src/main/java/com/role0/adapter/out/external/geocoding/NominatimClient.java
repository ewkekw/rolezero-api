package com.role0.adapter.out.external.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.role0.core.application.port.out.GeocodingServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adapter de saída para o serviço de Geocoding Reverso da OpenStreetMap (Nominatim).
 *
 * Política de uso do Nominatim:
 * - Header User-Agent obrigatório para identificação da aplicação.
 * - Rate limit de 1 request/segundo. Chamada é feita apenas 1 vez por evento (na criação).
 *   O endereço é persistido no banco — não recalculado a cada request.
 * - Fallback tolerante: erros retornam Optional.empty() sem propagar exceção para o caller.
 *
 * Ref: https://nominatim.org/release-docs/develop/api/Reverse/
 */
@Component
public class NominatimClient implements GeocodingServicePort {

    private static final Logger log = LoggerFactory.getLogger(NominatimClient.class);
    private final RestClient restClient;

    public NominatimClient(RestClient.Builder restClientBuilder,
                           @org.springframework.beans.factory.annotation.Value("${app.integrations.nominatim.base-url}") String baseUrl,
                           @org.springframework.beans.factory.annotation.Value("${app.integrations.nominatim.user-agent}") String userAgent) {
        @SuppressWarnings("null")
        String finalUrl = java.util.Objects.requireNonNull(baseUrl);
        @SuppressWarnings("null")
        String finalUserAgent = java.util.Objects.requireNonNull(userAgent);
        this.restClient = restClientBuilder
                .baseUrl(finalUrl)
                .defaultHeader("User-Agent", finalUserAgent)
                .defaultHeader("Accept-Language", "pt")
                .build();
    }

    @Override
    public Optional<String> resolverEnderecoPorCoordenadas(double lat, double lon) {
        log.info("Resolvendo endereço via Nominatim para lat={}, lon={}", lat, lon);
        try {
            NominatimResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reverse")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("format", "json")
                            .queryParam("accept-language", "pt")
                            .build())
                    .retrieve()
                    .body(NominatimResponse.class);

            if (response == null || response.address() == null) {
                log.warn("Nominatim retornou resposta vazia para lat={}, lon={}", lat, lon);
                return Optional.empty();
            }

            String enderecoFormatado = formatarEndereco(response.address());
            log.info("Endereço resolvido: '{}'", enderecoFormatado);
            return Optional.of(enderecoFormatado);

        } catch (Exception e) {
            log.warn("Falha tolerável ao consultar Nominatim para lat={}, lon={}: {}", lat, lon, e.getMessage());
            return Optional.empty();
        }
    }

    private String formatarEndereco(AddressRecord address) {
        // Monta: "Rua Augusta, Consolação, São Paulo" — conforme spec da documentação
        return Stream.of(address.road(), address.neighbourhood(), address.cityDistrict(), address.city())
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    // --- DTOs internos da resposta Nominatim (não vazam para fora deste adapter) ---

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NominatimResponse(
            @JsonProperty("address") AddressRecord address
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AddressRecord(
            @JsonProperty("road") String road,
            @JsonProperty("neighbourhood") String neighbourhood,
            @JsonProperty("city_district") String cityDistrict,
            @JsonProperty("city") String city,
            @JsonProperty("town") String town,
            @JsonProperty("village") String village
    ) {}
}
