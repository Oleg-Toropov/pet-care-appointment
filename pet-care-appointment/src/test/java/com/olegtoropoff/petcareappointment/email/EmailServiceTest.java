package com.olegtoropoff.petcareappointment.email;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmailServiceTest {
    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String jwtSecret = dotenv.get("JWT_SECRET");
        if (jwtSecret != null) {
            System.setProperty("JWT_SECRET", jwtSecret);
        } else {
            System.err.println("JWT_SECRET is not defined in .env file");
        }
    }

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendEmail() {
        try {
            emailService.sendEmail(
                    "toropovoleg1987@gmail.com",
                    "Тестовое письмо",
                    "Pet Care Appointment",
                    "<h1>Это тестовое письмо</h1><p>Проверка отправки письма через SMTP Яндекса.</p>"
            );
            System.out.println("Письмо успешно отправлено.");
        } catch (Exception e) {
            System.err.println("Ошибка отправки письма: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
