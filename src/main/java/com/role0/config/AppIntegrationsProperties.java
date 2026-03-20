package com.role0.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.integrations")
public class AppIntegrationsProperties {

    private final OpenWeather openweather = new OpenWeather();
    private final Nominatim nominatim = new Nominatim();

    public OpenWeather getOpenweather() {
        return openweather;
    }

    public Nominatim getNominatim() {
        return nominatim;
    }

    public static class OpenWeather {
        private String apiKey;
        private String baseUrl;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }

    public static class Nominatim {
        private String baseUrl;
        private String userAgent;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }
    }
}
