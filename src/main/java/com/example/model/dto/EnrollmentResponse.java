package com.example.model.dto;

import com.example.model.entity.Grade;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        String studentFullName,
        Long courseId,
        String courseName,
        String semester,
        Integer year,
        boolean paid,
        Grade grade
) {}
