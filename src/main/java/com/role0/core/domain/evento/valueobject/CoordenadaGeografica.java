package com.role0.core.domain.evento.valueobject;

import com.role0.core.domain.evento.exception.EventoDomainException;

public class CoordenadaGeografica {
    
    private final double latitude;
    private final double longitude;

    private static final int EARTH_RADIUS_KM = 6371;

    public CoordenadaGeografica(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new EventoDomainException("Latitude inválida");
        }
        if (longitude < -180 || longitude > 180) {
            throw new EventoDomainException("Longitude inválida");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public double calcularDistanciaMetros(CoordenadaGeografica outra) {
        double dLat = Math.toRadians(outra.latitude - this.latitude);
        double dLon = Math.toRadians(outra.longitude - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(outra.latitude)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000; // Convertendo para metros
    }
}
