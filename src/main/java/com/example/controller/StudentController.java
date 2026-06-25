package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.model.dto.*;
import com.example.model.entity.StudentStatus;
import com.example.service.StudentService;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Validated
@Tag(name = "Students", description = "CRUD operations, search, filtering and reports for students")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new student")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate email"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public StudentResponse create(@Valid @RequestBody StudentRequest req) {
        return studentService.create(req);
    }

    @GetMapping
    @Operation(summary = "Get all students",
            description = "Supports filtering by status and/or enrollment year with pagination")
    @ApiResponse(responseCode = "200", description = "Paginated list of students")
    public Page<StudentResponse> findAll(
            @Parameter(description = "Filter by student status") @RequestParam(required = false) StudentStatus status,
            @Parameter(description = "Filter by enrollment year") @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return studentService.findAll(status, year, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student found"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public StudentResponse findById(@PathVariable Long id) {
        return studentService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest req) {
        return studentService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete student by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Student deleted"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search students by first name, last name or email (partial match)")
    @ApiResponse(responseCode = "200", description = "Matching students")
    public List<StudentResponse> search(
            @Parameter(description = "Search query", required = true) @RequestParam String q) {
        return studentService.search(q);
    }

    @GetMapping("/unpaid")
    @Operation(summary = "Get students with at least one unpaid enrollment")
    public List<StudentResponse> unpaid() {
        return studentService.findWithUnpaid();
    }

    @GetMapping("/top")
    @Operation(summary = "Get top N students by GPA (weighted by credits)")
    public List<StudentResponse> top(
            @Parameter(description = "Number of top students to return")
            @RequestParam(defaultValue = "10") @Min(1) int n) {
        return studentService.findTopByGpa(n);
    }

    @GetMapping("/{id}/transcript")
    @Operation(summary = "Get full academic transcript with GPA for a student")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transcript with GPA"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    public TranscriptResponse transcript(@PathVariable Long id) {
        return studentService.getTranscript(id);
    }
}
