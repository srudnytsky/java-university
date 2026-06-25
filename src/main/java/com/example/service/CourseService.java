package com.example.service;

import com.example.exception.ResourceNotFoundException;
import com.example.model.dto.CourseRequest;
import com.example.model.dto.CourseResponse;
import com.example.model.entity.Course;
import com.example.model.entity.Teacher;
import com.example.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherService teacherService;

    public CourseResponse create(CourseRequest req) {
        Teacher teacher = req.teacherId() != null ? teacherService.getTeacherOrThrow(req.teacherId()) : null;
        Course course = Course.builder()
                .name(req.name())
                .credits(req.credits())
                .description(req.description())
                .teacher(teacher)
                .build();
        return toResponse(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public Page<CourseResponse> findAll(Long teacherId, Integer credits, Pageable pageable) {
        if (teacherId != null && credits != null) {
            return courseRepository.findByTeacherIdAndCredits(teacherId, credits, pageable).map(this::toResponse);
        } else if (teacherId != null) {
            return courseRepository.findByTeacherId(teacherId, pageable).map(this::toResponse);
        } else if (credits != null) {
            return courseRepository.findByCredits(credits, pageable).map(this::toResponse);
        }
        return courseRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CourseResponse findById(Long id) {
        return toResponse(getCourseOrThrow(id));
    }

    public CourseResponse update(Long id, CourseRequest req) {
        Course course = getCourseOrThrow(id);
        Teacher teacher = req.teacherId() != null ? teacherService.getTeacherOrThrow(req.teacherId()) : null;
        course.setName(req.name());
        course.setCredits(req.credits());
        course.setDescription(req.description());
        course.setTeacher(teacher);
        return toResponse(courseRepository.save(course));
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    public Course getCourseOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public CourseResponse toResponse(Course c) {
        String teacherName = c.getTeacher() != null
                ? c.getTeacher().getFirstName() + " " + c.getTeacher().getLastName()
                : null;
        Long teacherId = c.getTeacher() != null ? c.getTeacher().getId() : null;
        return new CourseResponse(c.getId(), c.getName(), c.getCredits(),
                c.getDescription(), teacherId, teacherName);
    }
}
