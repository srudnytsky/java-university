package com.example.model.dto;

import com.example.model.entity.Grade;
import jakarta.validation.constraints.NotNull;

public record GradeRequest(@NotNull Grade grade) {}