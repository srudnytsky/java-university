package com.example.model.dto;

import com.example.model.entity.TeacherPosition;
import java.time.LocalDate;

public record TeacherResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate dateOfBirth,
        TeacherPosition position
) {}