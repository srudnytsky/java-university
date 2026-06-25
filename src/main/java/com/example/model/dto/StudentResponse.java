package com.example.model.dto;

import com.example.model.entity.StudentStatus;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Integer enrollmentYear,
        StudentStatus status
) {}
