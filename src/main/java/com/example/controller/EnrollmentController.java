package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.model.dto.*;
import com.example.service.EnrollmentService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Enrollment management: create, grade, pay, GPA reports")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new enrollment",
            description = "Links studentId + courseId + semester. Defaults: grade=NA, paid=false")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Enrollment created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Student or course not found"),
            @ApiResponse(responseCode = "409", description = "Student already enrolled in this course/semester")
    })
    public EnrollmentResponse create(@Valid @RequestBody EnrollmentRequest req) {
        return enrollmentService.create(req);
    }

    @GetMapping
    @Operation(summary = "Get all enrollments")
    public List<EnrollmentResponse> findAll() {
        return enrollmentService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get enrollment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Enrollment found"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    public EnrollmentResponse findById(@PathVariable Long id) {
        return enrollmentService.findById(id);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all enrollments for a specific student")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student's enrollments"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public List<EnrollmentResponse> findByStudent(@PathVariable Long studentId) {
        return enrollmentService.findByStudent(studentId);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all enrollments for a specific course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course's enrollments"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public List<EnrollmentResponse> findByCourse(@PathVariable Long courseId) {
        return enrollmentService.findByCourse(courseId);
    }

    @PutMapping("/{id}/grade")
    @Operation(summary = "Set grade for an enrollment (A, B, C, D, F)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grade updated"),
            @ApiResponse(responseCode = "400", description = "Invalid grade value"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    public EnrollmentResponse setGrade(@PathVariable Long id, @Valid @RequestBody GradeRequest req) {
        return enrollmentService.setGrade(id, req.grade());
    }

    @PutMapping("/{id}/paid")
    @Operation(summary = "Mark enrollment as paid (sets paid=true)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marked as paid"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    public EnrollmentResponse markAsPaid(@PathVariable Long id) {
        return enrollmentService.markAsPaid(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete enrollment by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Enrollment deleted"),
            @ApiResponse(responseCode = "404", description = "Enrollment not found")
    })
    public void delete(@PathVariable Long id) {
        enrollmentService.delete(id);
    }

    @GetMapping("/gpa/course/{courseId}")
    @Operation(summary = "Get average GPA for all students in a specific course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Average GPA for the course"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public Map<String, Object> avgGpaByCourse(@PathVariable Long courseId) {
        return Map.of("courseId", courseId, "averageGpa", enrollmentService.getAverageGpaByCourse(courseId));
    }

    @GetMapping("/gpa/semester")
    @Operation(summary = "Get average GPA for a specific semester across all courses",
            description = "Semester format example: 'Fall-2024' or 'Spring-2025'")
    @ApiResponse(responseCode = "200", description = "Average GPA for the semester")
    public Map<String, Object> avgGpaBySemester(@RequestParam String semester) {
        return Map.of("semester", semester, "averageGpa", enrollmentService.getAverageGpaBySemester(semester));
    }
}
