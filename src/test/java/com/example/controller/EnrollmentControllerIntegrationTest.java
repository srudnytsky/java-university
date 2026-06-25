package com.example.controller;

import com.example.model.dto.CourseRequest;
import com.example.model.dto.EnrollmentRequest;
import com.example.model.dto.GradeRequest;
import com.example.model.dto.StudentRequest;
import com.example.model.entity.Grade;
import com.example.model.entity.StudentStatus;
import com.example.repository.CourseRepository;
import com.example.repository.EnrollmentRepository;
import com.example.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnrollmentControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StudentRepository studentRepository;
    @Autowired CourseRepository courseRepository;
    @Autowired EnrollmentRepository enrollmentRepository;

    private Long studentId;
    private Long courseId;

    @BeforeEach
    void setup() throws Exception {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();

        String sResp = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new StudentRequest("Test", "Student", "ts@test.com", 2022, StudentStatus.ACTIVE))))
                .andReturn().getResponse().getContentAsString();
        studentId = objectMapper.readTree(sResp).get("id").asLong();

        String cResp = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CourseRequest("Math", 4, "Mathematics", null))))
                .andReturn().getResponse().getContentAsString();
        courseId = objectMapper.readTree(cResp).get("id").asLong();
    }

    @Test
    void createEnrollment_shouldReturn201WithDefaults() throws Exception {
        EnrollmentRequest req = new EnrollmentRequest(studentId, courseId, "Fall", 2024);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.grade").value("NA"))
                .andExpect(jsonPath("$.paid").value(false));
    }

    @Test
    void createDuplicateEnrollment_shouldReturn409() throws Exception {
        EnrollmentRequest req = new EnrollmentRequest(studentId, courseId, "Fall", 2024);
        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void setGrade_shouldReturn200WithGrade() throws Exception {
        EnrollmentRequest req = new EnrollmentRequest(studentId, courseId, "Fall", 2024);
        String eResp = mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();
        Long enrollmentId = objectMapper.readTree(eResp).get("id").asLong();

        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GradeRequest(Grade.A))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value("A"));
    }

    @Test
    void markAsPaid_shouldReturn200Paid() throws Exception {
        EnrollmentRequest req = new EnrollmentRequest(studentId, courseId, "Spring", 2024);
        String eResp = mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();
        Long enrollmentId = objectMapper.readTree(eResp).get("id").asLong();

        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/paid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid").value(true));
    }
}