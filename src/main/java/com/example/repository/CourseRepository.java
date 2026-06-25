package com.example.repository;

import com.example.model.entity.Course;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);

    Page<Course> findByCredits(Integer credits, Pageable pageable);

    Page<Course> findByTeacherIdAndCredits(Long teacherId, Integer credits, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.credits >= :min AND c.credits <= :max")
    List<Course> findByCreditsRange(@Param("min") int min, @Param("max") int max);
}

