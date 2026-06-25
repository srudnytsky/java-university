package com.example.model.dto;

import com.example.model.entity.Grade;

public record TranscriptEntryResponse(
        Long enrollmentId,
        String courseName,
        Integer credits,
        String semester,
        Integer year,
        Grade grade,
        double gradePoints,
        boolean paid
) {}
