package com.olegtoropoff.petcareappointment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test_roles_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class RoleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllRoles_ReturnsListOfRoles() throws Exception {
        mockMvc.perform(get("/api/v1/roles/all-roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].name", is("ROLE_ADMIN")))
                .andExpect(jsonPath("$[1].name", is("ROLE_PATIENT")))
                .andExpect(jsonPath("$[2].name", is("ROLE_VET")));
    }

    @Test
    void getRoleById_ReturnsRole() throws Exception {
        mockMvc.perform(get("/api/v1/roles/role/get-by-id/role")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("ROLE_ADMIN")));
    }

    @Test
    void getRoleByName_ReturnsRole() throws Exception {
        mockMvc.perform(get("/api/v1/roles/role/get-by-name")
                        .param("roleName", "ROLE_PATIENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("ROLE_PATIENT")));
    }
}
