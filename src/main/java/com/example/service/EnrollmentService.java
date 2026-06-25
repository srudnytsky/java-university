package com.example.service;

import com.example.exception.DuplicateEnrollmentException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.dto.EnrollmentRequest;
import com.example.model.dto.EnrollmentResponse;
import com.example.model.entity.Course;
import com.example.model.entity.Enrollment;
import com.example.model.entity.Grade;
import com.example.model.entity.Student;
import com.example.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentResponse create(EnrollmentRequest req) {
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndYear(
                req.studentId(), req.courseId(), req.semester(), req.year())) {
            throw new DuplicateEnrollmentException(
                    "Student already enrolled in this course for " + req.semester() + " " + req.year());
        }
        Student student = studentService.getStudentOrThrow(req.studentId());
        Course course = courseService.getCourseOrThrow(req.courseId());

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .semester(req.semester())
                .year(req.year())
                .grade(Grade.NA)
                .paid(false)
                .build();
        return toResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findAll() {
        return enrollmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EnrollmentResponse findById(Long id) {
        return toResponse(getEnrollmentOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByStudent(Long studentId) {
        studentService.getStudentOrThrow(studentId); // validate exists
        return enrollmentRepository.findByStudentId(studentId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findByCourse(Long courseId) {
        courseService.getCourseOrThrow(courseId); // validate exists
        return enrollmentRepository.findByCourseId(courseId).stream().map(this::toResponse).toList();
    }

    public EnrollmentResponse setGrade(Long id, Grade grade) {
        Enrollment enrollment = getEnrollmentOrThrow(id);
        enrollment.setGrade(grade);
        return toResponse(enrollmentRepository.save(enrollment));
    }

    public EnrollmentResponse markAsPaid(Long id) {
        Enrollment enrollment = getEnrollmentOrThrow(id);
        enrollment.markAsPaid();
        return toResponse(enrollmentRepository.save(enrollment));
    }

    public void delete(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enrollment not found with id: " + id);
        }
        enrollmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Double getAverageGpaByCourse(Long courseId) {
        courseService.getCourseOrThrow(courseId);
        Double avg = enrollmentRepository.findAverageGpaByCourse(courseId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    @Transactional(readOnly = true)
    public Double getAverageGpaBySemester(String semester) {
        Double avg = enrollmentRepository.findAverageGpaBySemester(semester);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    private Enrollment getEnrollmentOrThrow(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }

    public EnrollmentResponse toResponse(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getStudent().getId(),
                e.getStudent().getFirstName() + " " + e.getStudent().getLastName(),
                e.getCourse().getId(),
                e.getCourse().getName(),
                e.getSemester(),
                e.getYear(),
                e.isPaid(),
                e.getGrade()
        );
    }
}
