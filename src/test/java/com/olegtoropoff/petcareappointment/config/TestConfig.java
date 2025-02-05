package com.olegtoropoff.petcareappointment.config;

import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.yandexs3.YandexS3Service;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public RabbitMQProducer rabbitMQProducer() {
        return mock(RabbitMQProducer.class);
    }

    @Bean
    public YandexS3Service yandexS3Service() {
        return mock(YandexS3Service.class);
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}