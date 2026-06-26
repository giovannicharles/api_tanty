package com.NTFOODS.Api_tanty.shared.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.time.Duration;

/**
 * CacheConfig - Configuration du cache Redis pour les performances
 * Configure le cache distribué pour améliorer les performances des requêtes fréquentes
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure les paramètres du cache Redis
     * Utilise la sérialisation JDK pour stocker les objets Java
     * @return Configuration du cache Redis
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // Construire la configuration du cache Redis
        return RedisCacheConfiguration.defaultCacheConfig()
            // Utiliser la sérialisation JDK pour les objets (plus stable que JSON)
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new JdkSerializationRedisSerializer()
                )
            )
            // Définir le temps de vie par défaut à 10 minutes
            .entryTtl(Duration.ofMinutes(10))
            // Ne pas mettre en cache les valeurs null
            .disableCachingNullValues();
    }
}
