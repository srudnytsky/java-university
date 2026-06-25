package com.example.service;


import com.example.exception.ResourceNotFoundException;
import com.example.model.dto.StudentRequest;
import com.example.model.dto.StudentResponse;
import com.example.model.entity.*;
import com.example.repository.EnrollmentRepository;
import com.example.repository.StudentRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock StudentRepository studentRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @InjectMocks StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L).firstName("John").lastName("Doe")
                .email("john@test.com").enrollmentYear(2022)
                .status(StudentStatus.ACTIVE).build();
    }

    @Test
    void create_shouldSaveAndReturnResponse() {
        StudentRequest req = new StudentRequest("John", "Doe", "john@test.com", 2022, StudentStatus.ACTIVE);
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(studentRepository.save(any())).thenReturn(student);

        StudentResponse resp = studentService.create(req);

        assertThat(resp.firstName()).isEqualTo("John");
        assertThat(resp.email()).isEqualTo("john@test.com");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void create_duplicateEmail_shouldThrow() {
        StudentRequest req = new StudentRequest("Jane", "Doe", "john@test.com", 2022, StudentStatus.ACTIVE);
        when(studentRepository.findByEmail("john@test.com")).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> studentService.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void findById_notFound_shouldThrow() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void findById_found_shouldReturnResponse() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        StudentResponse resp = studentService.findById(1L);

        assertThat(resp.id()).isEqualTo(1L);
        assertThat(resp.lastName()).isEqualTo("Doe");
    }

    @Test
    void delete_notFound_shouldThrow() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void calculateGpa_noGradedEnrollments_returnsZero() {
        when(enrollmentRepository.findByStudentId(1L)).thenReturn(List.of());

        double gpa = studentService.calculateGpa(1L);

        assertThat(gpa).isEqualTo(0.0);
    }

    @Test
    void calculateGpa_withGrades_returnsCorrectGpa() {
        Course course = Course.builder().id(1L).name("Math").credits(4).build();
        Enrollment e1 = Enrollment.builder().student(student).course(course).grade(Grade.A).paid(true).semester("Fall").year(2023).build();
        Enrollment e2 = Enrollment.builder().student(student).course(course).grade(Grade.B).paid(true).semester("Spring").year(2023).build();

        when(enrollmentRepository.findByStudentId(1L)).thenReturn(List.of(e1, e2));

        double gpa = studentService.calculateGpa(1L);

        // A=4.0*4 + B=3.0*4 = 16+12=28 / 8 credits = 3.5
        assertThat(gpa).isEqualTo(3.5);
    }
}