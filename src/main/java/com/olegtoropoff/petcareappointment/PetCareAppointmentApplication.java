package com.olegtoropoff.petcareappointment;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Pet Care Appointment Application.
 * <p>
 * This application provides an appointment scheduling system for veterinarians and pet owners, offering features like
 * user management, appointment booking, and review systems. The technology stack includes:
 * <ul>
 *     <li>Backend: Java 17, Spring Boot, Hibernate, MySQL</li>
 *     <li>Frontend: React, Bootstrap, Axios</li>
 *     <li>Asynchronous processing: RabbitMQ</li>
 *     <li>Containerization: Docker</li>
 *     <li>Cloud Storage: Yandex Cloud (S3-compatible)</li>
 * </ul>
 *
 * Features include:
 * <ul>
 *     <li>Secure user authentication with JWT</li>
 *     <li>Photo storage integration with Yandex Cloud S3</li>
 *     <li>Task scheduling via Spring's @EnableScheduling</li>
 * </ul>
 */
@SpringBootApplication
@EnableScheduling
public class PetCareAppointmentApplication {

	/**
	 * The entry point of the application.
	 * <p>
	 * This method initializes the application by:
	 * <ul>
	 *     <li>Loading environment variables from a `.env` file using the Dotenv library.</li>
	 *     <li>Setting critical system properties like `JWT_SECRET`, `AWS_ACCESS_KEY_ID`, and `AWS_SECRET_ACCESS_KEY` from
	 *     the environment variables.</li>
	 *     <li>Starting the Spring Boot application using {@link SpringApplication#run(Class, String[])}.</li>
	 *     <li>Displaying a confirmation message indicating the application is running successfully.</li>
	 * </ul>
	 *
	 * <p><strong>Environment Variables:</strong>
	 * <ul>
	 *     <li>`JWT_SECRET`: Secret key used for generating and validating JWT tokens.</li>
	 *     <li>`AWS_ACCESS_KEY_ID`: AWS access key for connecting to Yandex S3 (or AWS S3).</li>
	 *     <li>`AWS_SECRET_ACCESS_KEY`: AWS secret access key for connecting to Yandex S3 (or AWS S3).</li>
	 * </ul>
	 *
	 * <p>If any of the required environment variables are missing, a warning will be logged to the console.
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

		String awsAccessKeyId = dotenv.get("AWS_ACCESS_KEY_ID");
		if (awsAccessKeyId != null) {
			System.setProperty("AWS_ACCESS_KEY_ID", awsAccessKeyId);
		} else {
			System.err.println("AWS_ACCESS_KEY_ID is not defined in .env file");
		}

		String awsSecretAccessKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
		if (awsSecretAccessKey != null) {
			System.setProperty("AWS_SECRET_ACCESS_KEY", awsSecretAccessKey);
		} else {
			System.err.println("AWS_SECRET_ACCESS_KEY is not defined in .env file");
		}

		SpringApplication.run(PetCareAppointmentApplication.class, args);
		System.out.println("\uD83D\uDC3E Pet Care Appointment API is running! Your pets are in safe hands. ");
	}
}