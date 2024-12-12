package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        user.setGender("Male");
        user.setPhoneNumber("89121234567");
        user.setEmail("ivanov@gmail.com");
        user.setUserType("PATIENT");
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Test
    public void testGetById_ValidUserId_ReturnsUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь найден"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    public void testGetById_InvalidUserId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/user/100"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Извините, пользователь не найден"));
    }


}

