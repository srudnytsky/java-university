package com.example.model.dto;

import com.example.model.entity.TeacherPosition;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record TeacherRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email,
        LocalDate dateOfBirth,
        @NotNull TeacherPosition position
) {}
