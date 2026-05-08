package com.role0.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de Cache do Role0.
 *
 * <p>Estratégia:
 * <ul>
 *   <li>Se {@code REDIS_URL} estiver definida, o Spring auto-configura o Redis normalmente.</li>
 *   <li>Se {@code REDIS_URL} <strong>não</strong> estiver definida (Render free, local sem Redis),
 *       usa {@link ConcurrentMapCacheManager} (in-memory, mesmo processo).</li>
 * </ul>
 *
 * <p>Isso evita o {@code RedisConnectionFailureException} no Render free tier.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache in-memory: ativado apenas quando REDIS_URL e spring.redis.host
     * não estão configurados (ausência do Redis).
     *
     * <p>A anotação {@code @ConditionalOnMissingBean(CacheManager.class)} garante que,
     * se o Spring auto-configurar um {@code RedisCacheManager} (porque REDIS_URL foi
     * fornecida), este bean será ignorado.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.data.redis.url", havingValue = "", matchIfMissing = true)
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager inMemoryCacheManager() {
        ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager();
        manager.setCacheNames(Arrays.asList(
            "eventosCache",
            "eventosProximos",
            "weatherCache",
            "perfilUsuario",
            "tokenBlacklist"
        ));
        manager.setAllowNullValues(false);
        return manager;
    }
}
