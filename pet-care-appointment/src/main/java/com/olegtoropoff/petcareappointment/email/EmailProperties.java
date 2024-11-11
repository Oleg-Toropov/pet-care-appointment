package com.olegtoropoff.petcareappointment.email;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean auth;
    private boolean starttls;
}
