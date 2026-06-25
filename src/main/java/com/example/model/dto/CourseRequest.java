package com.example.model.dto;

import jakarta.validation.constraints.*;

public record CourseRequest(
        @NotBlank String name,
        @Min(1) @Max(30) @NotNull Integer credits,
        String description,
        Long teacherId
) {}
