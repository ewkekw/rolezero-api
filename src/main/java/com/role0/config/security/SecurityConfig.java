package com.role0.config.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.role0.config.AppIntegrationsProperties;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({AppIntegrationsProperties.class, JwtProperties.class})
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Skill: Auth Patterns & Security Auditor
     * - Configuração CSRF Desabilitada pois operamos via Headers JWT (livres de ataques via Browser Cookies).
     * - Session Management STATELESS para escalar horizontalmente sem Sticky Sessions.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/register",
                    "/api/v1/auth/login",
                    "/api/v1/auth/sso",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/**"
                ).permitAll() // Libera Swagger UI, Actuator e endpoints públicos de auth
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/events/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/users/{userId}").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/users/{userId}/reviews").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
