package com.example.model.dto;

import java.util.List;

public record TranscriptResponse(
        Long studentId,
        String studentFullName,
        String email,
        double gpa,
        int totalCredits,
        List<TranscriptEntryResponse> entries
) {}