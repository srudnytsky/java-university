package com.example.model.dto;

import com.example.model.entity.StudentStatus;
import jakarta.validation.constraints.*;

public record StudentRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @Email(message = "Invalid email") @NotBlank(message = "Email is required") String email,
        @NotNull(message = "Enrollment year is required") @Min(2000) @Max(2100) Integer enrollmentYear,
        @NotNull(message = "Status is required") StudentStatus status
) {}
