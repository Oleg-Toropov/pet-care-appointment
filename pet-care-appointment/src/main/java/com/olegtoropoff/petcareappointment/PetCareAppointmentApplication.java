package com.olegtoropoff.petcareappointment;

import io.github.cdimascio.dotenv.Dotenv;
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
 */

@SpringBootApplication
@EnableScheduling
public class PetCareAppointmentApplication {

	/**
	 * The entry point of the application.
	 *
	 * <p>Performs the following tasks:
	 * <ul>
	 *   <li>Loads environment variables from the `.env` file using the Dotenv library.</li>
	 *   <li>Sets the system property "JWT_SECRET" if the corresponding environment variable is present.</li>
	 *   <li>Logs a warning to the console if "JWT_SECRET" is not defined in the `.env` file.</li>
	 *   <li>Starts the Spring Boot application.</li>
	 *   <li>Displays a confirmation message in the console indicating that the application is running.</li>
	 * </ul>
	 *
	 * @param args Command-line arguments passed to the application.
	 */
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