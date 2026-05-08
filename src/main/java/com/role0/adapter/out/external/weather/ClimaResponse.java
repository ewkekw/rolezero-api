package com.role0.adapter.out.external.weather;

public record ClimaResponse(
        Double temp,
        String condition,
        String icon
) {}
