package com.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.example.model.dto.TeacherRequest;
import com.example.model.entity.TeacherPosition;
import com.example.repository.TeacherRepository;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TeacherControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired TeacherRepository teacherRepository;

    @BeforeEach
    void cleanup() {
        teacherRepository.deleteAll();
    }

    @Test
    @Order(1)
    void createTeacher_shouldReturn201() throws Exception {
        TeacherRequest req = new TeacherRequest("Ivan", "Petrenko",
                "ivan@univ.ua", LocalDate.of(1980, 5, 15), TeacherPosition.PROFESSOR);

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ivan"))
                .andExpect(jsonPath("$.position").value("PROFESSOR"));
    }

    @Test
    @Order(2)
    void getTeacher_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/teachers/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @Order(3)
    void createTeacher_duplicateEmail_shouldReturn400() throws Exception {
        TeacherRequest req = new TeacherRequest("Olha", "Koval",
                "olha@univ.ua", null, TeacherPosition.LECTURER);
        mockMvc.perform(post("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    void updateTeacher_shouldReturn200WithUpdatedPosition() throws Exception {
        TeacherRequest create = new TeacherRequest("Serhiy", "Melnyk",
                "serhiy@univ.ua", null, TeacherPosition.ASSISTANT);
        String resp = mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(resp).get("id").asLong();

        TeacherRequest update = new TeacherRequest("Serhiy", "Melnyk",
                "serhiy@univ.ua", null, TeacherPosition.PROFESSOR);
        mockMvc.perform(put("/api/teachers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("PROFESSOR"));
    }

    @Test
    @Order(5)
    void deleteTeacher_shouldReturn204() throws Exception {
        TeacherRequest req = new TeacherRequest("Test", "Delete",
                "del@univ.ua", null, TeacherPosition.LECTURER);
        String resp = mockMvc.perform(post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(resp).get("id").asLong();

        mockMvc.perform(delete("/api/teachers/" + id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/teachers/" + id))
                .andExpect(status().isNotFound());
    }
}
