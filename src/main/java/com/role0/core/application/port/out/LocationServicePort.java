package com.role0.core.application.port.out;

import com.role0.core.domain.evento.valueobject.CoordenadaGeografica;

public interface LocationServicePort {
    /**
     * Validação externa caso havesine não seja suficiente (Ex: rota caminhável)
     * Por padrão não necessitaremos de API externa pois o Vo resolve.
     */
    double calcularDistanciaEntrePontos(CoordenadaGeografica origem, CoordenadaGeografica destino);
}
