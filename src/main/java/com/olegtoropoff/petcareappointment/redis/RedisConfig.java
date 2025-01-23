package com.olegtoropoff.petcareappointment.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuration class for Redis caching.
 * <p>
 * This class configures the Redis connection, cache management, and serialization for keys and values.
 * It is annotated with {@code @Configuration} to indicate that it is a Spring configuration class
 * and {@code @EnableCaching} to enable Spring's annotation-driven caching mechanism.
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Creates and configures a {@link RedisConnectionFactory} for establishing connections to the Redis server.
     *
     * @return a new instance of {@link LettuceConnectionFactory} for Redis connections.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }


    /**
     * Configures and returns a {@link RedisCacheManager} for managing Redis caches.
     * <p>
     * The cache configuration specifies key and value serializers for Redis and sets a time-to-live (TTL)
     * of 10 minutes for cache entries.
     * </p>
     *
     * @param connectionFactory the {@link RedisConnectionFactory} used to create Redis connections.
     * @return a configured {@link RedisCacheManager} instance.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(new ObjectMapper().registerModule(new JavaTimeModule()))))
                .entryTtl(Duration.ofMinutes(10));  // time to live (TTL)

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * Configures and returns a {@link RedisTemplate} for interacting with Redis.
     * <p>
     * This template is configured with serializers for keys and values, where keys are serialized as strings
     * and values are serialized using Jackson with JSON format.
     * </p>
     *
     * @param connectionFactory the {@link RedisConnectionFactory} used to create Redis connections.
     * @return a configured {@link RedisTemplate} instance for Redis operations.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}