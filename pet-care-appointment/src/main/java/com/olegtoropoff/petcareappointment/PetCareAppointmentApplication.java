package com.olegtoropoff.petcareappointment;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetCareAppointmentApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
//		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET")); TODO CHANGE
		String jwtSecret = dotenv.get("JWT_SECRET");
		if (jwtSecret != null) {
			System.setProperty("JWT_SECRET", jwtSecret);
		} else {
			System.err.println("JWT_SECRET is not defined in .env file");
		}

		SpringApplication.run(PetCareAppointmentApplication.class, args);
	}
}
