package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.model.dto.*;
import com.example.model.entity.TeacherPosition;
import com.example.service.TeacherService;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "CRUD operations for teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new teacher")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Teacher created"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate email")
    })
    public TeacherResponse create(@Valid @RequestBody TeacherRequest req) {
        return teacherService.create(req);
    }

    @GetMapping
    @Operation(summary = "Get all teachers", description = "Optional filter by position")
    @ApiResponse(responseCode = "200", description = "Paginated list of teachers")
    public Page<TeacherResponse> findAll(
            @RequestParam(required = false) TeacherPosition position,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName") String sort) {
        return teacherService.findAll(position, PageRequest.of(page, size, Sort.by(sort)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get teacher by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teacher found"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    public TeacherResponse findById(@PathVariable Long id) {
        return teacherService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update teacher by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teacher updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    public TeacherResponse update(@PathVariable Long id, @Valid @RequestBody TeacherRequest req) {
        return teacherService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete teacher by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Teacher deleted"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    public void delete(@PathVariable Long id) {
        teacherService.delete(id);
    }
}
