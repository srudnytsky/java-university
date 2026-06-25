package com.example.service;

import com.example.exception.ResourceNotFoundException;
import com.example.model.dto.StudentRequest;
import com.example.model.dto.StudentResponse;
import com.example.model.dto.TranscriptEntryResponse;
import com.example.model.dto.TranscriptResponse;
import com.example.model.entity.Enrollment;
import com.example.model.entity.Student;
import com.example.model.entity.StudentStatus;
import com.example.repository.EnrollmentRepository;
import com.example.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentResponse create(StudentRequest req) {
        if (studentRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + req.email());
        }
        Student student = Student.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .enrollmentYear(req.enrollmentYear())
                .status(req.status())
                .build();
        return toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public Page<StudentResponse> findAll(StudentStatus status, Integer year, Pageable pageable) {
        Page<Student> page;
        if (status != null && year != null) {
            page = studentRepository.findByStatusAndEnrollmentYear(status, year, pageable);
        } else if (status != null) {
            page = studentRepository.findByStatus(status, pageable);
        } else if (year != null) {
            page = studentRepository.findByEnrollmentYear(year, pageable);
        } else {
            page = studentRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return toResponse(getStudentOrThrow(id));
    }

    public StudentResponse update(Long id, StudentRequest req) {
        Student student = getStudentOrThrow(id);
        if (!student.getEmail().equals(req.email()) &&
                studentRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + req.email());
        }
        student.setFirstName(req.firstName());
        student.setLastName(req.lastName());
        student.setEmail(req.email());
        student.setEnrollmentYear(req.enrollmentYear());
        student.setStatus(req.status());
        return toResponse(studentRepository.save(student));
    }

    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> search(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query must not be blank");
        }
        return studentRepository.searchByNameOrEmail(query)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findWithUnpaid() {
        return studentRepository.findStudentsWithUnpaidEnrollments()
                .stream().map(this::toResponse).toList();
    }

    /**
     * Returns top-N students ordered by weighted GPA (descending).
     * Uses a DB-level JPQL query — no N+1 problem.
     */
    @Transactional(readOnly = true)
    public List<StudentResponse> findTopByGpa(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be > 0");
        Pageable pageable = PageRequest.of(0, n);
        return studentRepository.findTopStudentsByGpa(pageable)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TranscriptResponse getTranscript(Long studentId) {
        Student student = getStudentOrThrow(studentId);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        List<TranscriptEntryResponse> entries = enrollments.stream()
                .map(e -> new TranscriptEntryResponse(
                        e.getId(),
                        e.getCourse().getName(),
                        e.getCourse().getCredits(),
                        e.getSemester(),
                        e.getYear(),
                        e.getGrade(),
                        e.getGrade().getPoints(),
                        e.isPaid()
                ))
                .toList();

        double gpa = calculateGpaFromEnrollments(enrollments);
        int totalCredits = enrollments.stream()
                .filter(e -> e.getGrade().isGraded())
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();

        return new TranscriptResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                student.getEmail(),
                gpa,
                totalCredits,
                entries
        );
    }

    public double calculateGpa(Long studentId) {
        return calculateGpaFromEnrollments(enrollmentRepository.findByStudentId(studentId));
    }

    private double calculateGpaFromEnrollments(List<Enrollment> enrollments) {
        List<Enrollment> graded = enrollments.stream()
                .filter(e -> e.getGrade().isGraded())
                .toList();
        if (graded.isEmpty()) return 0.0;

        double totalPoints = graded.stream()
                .mapToDouble(e -> e.getGrade().getPoints() * e.getCourse().getCredits())
                .sum();
        int totalCredits = graded.stream()
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
        return totalCredits == 0 ? 0.0 : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    public Student getStudentOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public StudentResponse toResponse(Student s) {
        return new StudentResponse(s.getId(), s.getFirstName(), s.getLastName(),
                s.getEmail(), s.getEnrollmentYear(), s.getStatus());
    }
}
