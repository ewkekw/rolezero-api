package com.role0.core.application.port.out;

import java.util.Optional;

public interface WeatherServicePort {
    
    Optional<PrevisaoClima> getPrevisaoPorCoordenadas(double lat, double lon);

    record PrevisaoClima(Double temp, String condition, String icon) {}
}
