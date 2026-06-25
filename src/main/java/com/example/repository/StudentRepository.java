package com.example.repository;

import com.example.model.entity.Student;
import com.example.model.entity.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    Page<Student> findByEnrollmentYear(Integer year, Pageable pageable);

    Page<Student> findByStatusAndEnrollmentYear(StudentStatus status, Integer year, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
            "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.lastName)  LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(s.email)     LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Student> searchByNameOrEmail(@Param("q") String query);

    @Query("SELECT DISTINCT s FROM Student s JOIN s.enrollments e WHERE e.paid = false")
    List<Student> findStudentsWithUnpaidEnrollments();

    /**
     * Returns students ordered by weighted GPA descending.
     * Only students who have at least one graded enrollment (grade != NA) are returned.
     */
    @Query("""
        SELECT s FROM Student s
        WHERE EXISTS (
            SELECT 1 FROM Enrollment e WHERE e.student = s AND e.grade <> 'NA'
        )
        ORDER BY (
            SELECT COALESCE(SUM(
                CASE e2.grade
                    WHEN 'A' THEN 4.0 * e2.course.credits
                    WHEN 'B' THEN 3.0 * e2.course.credits
                    WHEN 'C' THEN 2.0 * e2.course.credits
                    WHEN 'D' THEN 1.0 * e2.course.credits
                    WHEN 'F' THEN 0.0
                    ELSE 0.0
                END
            ), 0.0) / NULLIF(SUM(CASE WHEN e2.grade <> 'NA' THEN e2.course.credits ELSE 0 END), 0)
            FROM Enrollment e2 WHERE e2.student = s AND e2.grade <> 'NA'
        ) DESC
        """)
    List<Student> findTopStudentsByGpa(Pageable pageable);

    Optional<Student> findByEmail(String email);
}
