package com.example.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id", "semester", "year"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Enrollment implements Payable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank(message = "Semester is required")
    @Column(nullable = false)
    private String semester;

    @Min(value = 2000) @Max(value = 2100)
    @Column(nullable = false)
    private Integer year;

    @Builder.Default
    private boolean paid = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Grade grade = Grade.NA;

    @Override
    public boolean isPaid() { return paid; }

    @Override
    public void markAsPaid() { this.paid = true; }
}
