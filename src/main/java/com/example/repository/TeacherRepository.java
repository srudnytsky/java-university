package com.example.repository;

import com.example.model.entity.Teacher;
import com.example.model.entity.TeacherPosition;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
    Page<Teacher> findByPosition(TeacherPosition position, Pageable pageable);
}