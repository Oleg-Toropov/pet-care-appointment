package com.olegtoropoff.petcareappointment;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Pet Care Appointment Application.
 * <p>
 * Technology stack:
 * <ul>
 *     <li>Backend: Java 17, Spring Boot, Hibernate, MySQL</li>
 *     <li>Frontend: React, Bootstrap, Axios</li>
 *     <li>Asynchronous processing: RabbitMQ</li>
 *     <li>Containerization: Docker</li>
 * </ul>
 * </p>
 */

@OpenAPIDefinition(
		info = @Info(
				title = "Pet Care Appointment API",
				version = "1.0",
				description = """
            A full-featured application for managing veterinary appointments, users, pets, and reviews.
            Built with Spring Boot, MySQL, RabbitMQ, React, and Docker. Full API documentation is available here.
        """
		)
)

@SpringBootApplication
@EnableScheduling
public class PetCareAppointmentApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		String jwtSecret = dotenv.get("JWT_SECRET");
		if (jwtSecret != null) {
			System.setProperty("JWT_SECRET", jwtSecret);
		} else {
			System.err.println("JWT_SECRET is not defined in .env file");
		}

		SpringApplication.run(PetCareAppointmentApplication.class, args);
		System.out.println("\uD83D\uDC3E Pet Care Appointment API is running! Your pets are in safe hands. ");
	}
}