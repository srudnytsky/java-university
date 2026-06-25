package com.example.model.dto;

public record CourseResponse(
        Long id,
        String name,
        Integer credits,
        String description,
        Long teacherId,
        String teacherFullName
) {}
