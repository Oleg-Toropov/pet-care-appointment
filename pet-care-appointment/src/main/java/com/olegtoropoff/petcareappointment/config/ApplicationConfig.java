package com.olegtoropoff.petcareappointment.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for application-wide beans and settings.
 * <p>
 * This class provides centralized configuration for beans that can
 * be shared across the application.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Bean definition for {@link ModelMapper}.
     * <p>
     * This bean provides object mapping capabilities to the application,
     * allowing for easy conversion between DTOs and entities.
     *
     * @return a new instance of {@link ModelMapper}
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
