package com.olegtoropoff.petcareappointment.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

/**
 * Configuration class for setting up Redis caching and serialization in the application.
 * This configuration is only active when the application is not running in the "test" profile.
 * It enables caching, configures Redis connection, and defines serialization settings.
 */
@Profile("!test")
@Configuration
@EnableCaching
public class RedisConfig {

    /** Default Time-To-Live (TTL) duration for cache entries. */
    private static final Duration CACHE_TTL = Duration.ofMinutes(60);

    /**
     * Creates a {@link RedisConnectionFactory} using Lettuce.
     *
     * @return a {@link LettuceConnectionFactory} instance that manages Redis connections.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * Configures a {@link RedisCacheManager} with JSON serialization and a default TTL.
     *
     * @param connectionFactory the Redis connection factory.
     * @return a configured {@link RedisCacheManager} instance.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = createObjectMapper();

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(CACHE_TTL);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * Configures a {@link RedisTemplate} for interacting with Redis storage.
     * This template enables efficient serialization and deserialization of objects using JSON.
     *
     * @param connectionFactory the Redis connection factory.
     * @return a fully configured {@link RedisTemplate} instance.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = createObjectMapper();

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        return template;
    }

    /**
     * Creates and configures an {@link ObjectMapper} for JSON serialization,
     * including support for Java 8 date and time API.
     *
     * @return a configured {@link ObjectMapper} instance.
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}