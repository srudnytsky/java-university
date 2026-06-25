package com.example.controller;

import com.example.model.dto.StudentRequest;
import com.example.model.entity.StudentStatus;
import com.example.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StudentRepository studentRepository;

    @BeforeEach
    void cleanup() {
        studentRepository.deleteAll();
    }

    @Test
    @Order(1)
    void createStudent_shouldReturn201() throws Exception {
        StudentRequest req = new StudentRequest("John", "Doe", "john@test.com", 2022, StudentStatus.ACTIVE);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @Order(2)
    void createStudent_invalidEmail_shouldReturn400() throws Exception {
        StudentRequest req = new StudentRequest("Jane", "Doe", "not-an-email", 2022, StudentStatus.ACTIVE);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void getStudent_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/students/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void getAllStudents_shouldReturn200WithList() throws Exception {
        // create one first
        StudentRequest req = new StudentRequest("Alice", "Smith", "alice@test.com", 2021, StudentStatus.ACTIVE);
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].firstName").value("Alice"));
    }

    @Test
    @Order(5)
    void deleteStudent_shouldReturn204() throws Exception {
        StudentRequest req = new StudentRequest("Bob", "Marley", "bob@test.com", 2020, StudentStatus.GRADUATED);
        String response = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/students/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/students/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    void updateStudent_shouldReturn200() throws Exception {
        StudentRequest createReq = new StudentRequest("Tom", "Jones", "tom@test.com", 2023, StudentStatus.ACTIVE);
        String response = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        StudentRequest updateReq = new StudentRequest("Tom", "Jones", "tom@test.com", 2023, StudentStatus.ON_LEAVE);
        mockMvc.perform(put("/api/students/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_LEAVE"));
    }

    @Test
    @Order(7)
    void searchStudents_shouldReturnMatching() throws Exception {
        StudentRequest req = new StudentRequest("Maria", "Garcia", "maria@test.com", 2022, StudentStatus.ACTIVE);
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(get("/api/students/search?q=maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("maria@test.com"));
    }
}
