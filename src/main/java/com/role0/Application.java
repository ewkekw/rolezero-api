package com.role0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class,
    org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
@EnableCaching
@EnableAsync // Habilita processamento paralelo (combinado com Virtual Threads via property)
@EnableJpaRepositories(basePackages = "com.role0.adapter.out.persistence.repository")
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        // Skill: Data Engineer & DevSecOps - Inicialização limpa da JVM e do Contexto Spring
        SpringApplication.run(Application.class, args);
        log.info("🚀 ROLE-ZERO BANDEIRA PRETA. CORE API INICIADA COM SUCESSO. (Loom / PostGIS Ready)");
    }
}
