package com.example.service;

import com.example.exception.DuplicateEnrollmentException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.dto.EnrollmentRequest;
import com.example.model.dto.EnrollmentResponse;
import com.example.model.entity.*;
import com.example.repository.EnrollmentRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock EnrollmentRepository enrollmentRepository;
    @Mock StudentService studentService;
    @Mock CourseService courseService;
    @InjectMocks EnrollmentService enrollmentService;

    private Student student;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        student = Student.builder().id(1L).firstName("John").lastName("Doe")
                .email("john@test.com").enrollmentYear(2022).status(StudentStatus.ACTIVE).build();
        course = Course.builder().id(1L).name("Math").credits(4).build();
        enrollment = Enrollment.builder().id(1L).student(student).course(course)
                .semester("Fall").year(2024).grade(Grade.NA).paid(false).build();
    }

    @Test
    void create_duplicate_shouldThrow() {
        EnrollmentRequest req = new EnrollmentRequest(1L, 1L, "Fall", 2024);
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndYear(1L, 1L, "Fall", 2024))
                .thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.create(req))
                .isInstanceOf(DuplicateEnrollmentException.class);
    }

    @Test
    void create_valid_shouldSaveWithDefaultValues() {
        EnrollmentRequest req = new EnrollmentRequest(1L, 1L, "Fall", 2024);
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndYear(1L, 1L, "Fall", 2024))
                .thenReturn(false);
        when(studentService.getStudentOrThrow(1L)).thenReturn(student);
        when(courseService.getCourseOrThrow(1L)).thenReturn(course);
        when(enrollmentRepository.save(any())).thenReturn(enrollment);

        EnrollmentResponse resp = enrollmentService.create(req);

        assertThat(resp.grade()).isEqualTo(Grade.NA);
        assertThat(resp.paid()).isFalse();
    }

    @Test
    void setGrade_shouldUpdateGrade() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        enrollment.setGrade(Grade.A);
        when(enrollmentRepository.save(any())).thenReturn(enrollment);

        EnrollmentResponse resp = enrollmentService.setGrade(1L, Grade.A);

        assertThat(resp.grade()).isEqualTo(Grade.A);
    }

    @Test
    void markAsPaid_shouldSetPaidTrue() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        enrollment.markAsPaid();
        when(enrollmentRepository.save(any())).thenReturn(enrollment);

        EnrollmentResponse resp = enrollmentService.markAsPaid(1L);

        assertThat(resp.paid()).isTrue();
    }

    @Test
    void findById_notFound_shouldThrow() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
