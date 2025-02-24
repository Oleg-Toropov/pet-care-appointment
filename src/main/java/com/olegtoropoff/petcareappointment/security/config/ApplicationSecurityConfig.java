package com.olegtoropoff.petcareappointment.security.config;

import com.olegtoropoff.petcareappointment.security.jwt.AuthTokenFilter;
import com.olegtoropoff.petcareappointment.security.jwt.JwtAuthEntryPoint;
import com.olegtoropoff.petcareappointment.security.user.UPCUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuration class for application security.
 * <p>
 * This class sets up security configurations including authentication,
 * authorization, CORS settings, and session management for the application.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig {

    private final UPCUserDetailsService upcUserDetailsService;
    private final JwtAuthEntryPoint authEntryPoint;

    private static final List<String> SECURED_URLS = List.of(
            "/api/v1/appointments/book-appointment",
            "/api/v1/reviews/**");

    /**
     * Bean definition for JWT authentication token filter.
     *
     * @return the {@link AuthTokenFilter} bean
     */
    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Bean definition for password encoder using BCrypt hashing.
     *
     * @return the {@link PasswordEncoder} bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean definition for the authentication manager.
     *
     * @param authConfig the authentication configuration
     * @return the {@link AuthenticationManager} bean
     * @throws Exception if an error occurs while retrieving the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configures the DAO-based authentication provider.
     *
     * @return the {@link DaoAuthenticationProvider} bean
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(upcUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configures the security filter chain for HTTP security.
     *
     * @param http the {@link HttpSecurity} object
     * @return the {@link SecurityFilterChain} bean
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers(SECURED_URLS.toArray(String[]::new)).authenticated()
                        .anyRequest().permitAll());
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings.
     *
     * @return the {@link WebMvcConfigurer} bean
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost", "http://localhost:5173", "http://51.250.1.8", "https://doctor-aibolit.pro")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
