package com.example.repository;

import com.example.model.entity.Enrollment;
import com.example.model.entity.Grade;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseIdAndSemesterAndYear(Long studentId, Long courseId, String semester, Integer year);

    Optional<Enrollment> findByStudentIdAndCourseIdAndSemesterAndYear(Long studentId, Long courseId, String semester, Integer year);

    @Query("SELECT AVG(CASE e.grade WHEN 'A' THEN 4.0 WHEN 'B' THEN 3.0 WHEN 'C' THEN 2.0 WHEN 'D' THEN 1.0 WHEN 'F' THEN 0.0 ELSE null END) FROM Enrollment e WHERE e.course.id = :courseId AND e.grade <> 'NA'")
    Double findAverageGpaByCourse(@Param("courseId") Long courseId);

    @Query("SELECT AVG(CASE e.grade WHEN 'A' THEN 4.0 WHEN 'B' THEN 3.0 WHEN 'C' THEN 2.0 WHEN 'D' THEN 1.0 WHEN 'F' THEN 0.0 ELSE null END) FROM Enrollment e WHERE e.semester = :semester AND e.grade <> 'NA'")
    Double findAverageGpaBySemester(@Param("semester") String semester);

    List<Enrollment> findByStudentIdAndGradeNot(Long studentId, Grade grade);
}
