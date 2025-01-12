package com.olegtoropoff.petcareappointment.email;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for email settings.
 * <p>
 * This class binds to properties prefixed with "spring.mail" in the application configuration file
 * and provides easy access to email-related properties such as host, port, username, and password.
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperties {

    /**
     * SMTP server host.
     */
    private String host;

    /**
     * SMTP server port.
     */
    private int port;

    /**
     * Username for SMTP authentication.
     */
    private String username;

    /**
     * Password for SMTP authentication.
     */
    private String password;

    /**
     * Indicates whether authentication is required for the SMTP server.
     */
    private boolean auth;

    /**
     * Indicates whether STARTTLS encryption is enabled for the SMTP server.
     */
    private boolean starttls;
}
