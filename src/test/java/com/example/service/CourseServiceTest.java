package com.example.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.exception.ResourceNotFoundException;
import com.example.model.dto.*;
import com.example.model.entity.*;
import com.example.repository.CourseRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock CourseRepository courseRepository;
    @Mock TeacherService teacherService;
    @InjectMocks CourseService courseService;

    private Course course;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = Teacher.builder().id(1L).firstName("John").lastName("Prof")
                .email("prof@test.com").position(TeacherPosition.PROFESSOR).build();
        course = Course.builder().id(1L).name("Algorithms").credits(4)
                .description("Data structures and algorithms").teacher(teacher).build();
    }

    @Test
    void create_withoutTeacher_shouldSaveCourse() {
        CourseRequest req = new CourseRequest("Math", 3, "Mathematics", null);
        Course saved = Course.builder().id(2L).name("Math").credits(3).build();
        when(courseRepository.save(any())).thenReturn(saved);

        CourseResponse resp = courseService.create(req);

        assertThat(resp.name()).isEqualTo("Math");
        assertThat(resp.credits()).isEqualTo(3);
        assertThat(resp.teacherId()).isNull();
    }

    @Test
    void create_withTeacher_shouldAssignTeacher() {
        CourseRequest req = new CourseRequest("Algorithms", 4, "DSA", 1L);
        when(teacherService.getTeacherOrThrow(1L)).thenReturn(teacher);
        when(courseRepository.save(any())).thenReturn(course);

        CourseResponse resp = courseService.create(req);

        assertThat(resp.teacherId()).isEqualTo(1L);
        assertThat(resp.teacherFullName()).isEqualTo("John Prof");
    }

    @Test
    void findById_notFound_shouldThrow() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found");
    }

    @Test
    void delete_notFound_shouldThrow() {
        when(courseRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> courseService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_found_shouldReturnResponse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseResponse resp = courseService.findById(1L);

        assertThat(resp.name()).isEqualTo("Algorithms");
        assertThat(resp.credits()).isEqualTo(4);
    }
}