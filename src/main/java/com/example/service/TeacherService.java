package com.example.service;

import com.example.exception.ResourceNotFoundException;
import com.example.model.dto.TeacherRequest;
import com.example.model.dto.TeacherResponse;
import com.example.model.entity.Teacher;
import com.example.model.entity.TeacherPosition;
import com.example.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherResponse create(TeacherRequest req) {
        if (teacherRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + req.email());
        }
        Teacher teacher = Teacher.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .dateOfBirth(req.dateOfBirth())
                .position(req.position())
                .build();
        return toResponse(teacherRepository.save(teacher));
    }

    @Transactional(readOnly = true)
    public Page<TeacherResponse> findAll(TeacherPosition position, Pageable pageable) {
        if (position != null) {
            return teacherRepository.findByPosition(position, pageable).map(this::toResponse);
        }
        return teacherRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TeacherResponse findById(Long id) {
        return toResponse(getTeacherOrThrow(id));
    }

    public TeacherResponse update(Long id, TeacherRequest req) {
        Teacher teacher = getTeacherOrThrow(id);
        if (!teacher.getEmail().equals(req.email()) &&
                teacherRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + req.email());
        }
        teacher.setFirstName(req.firstName());
        teacher.setLastName(req.lastName());
        teacher.setEmail(req.email());
        teacher.setDateOfBirth(req.dateOfBirth());
        teacher.setPosition(req.position());
        return toResponse(teacherRepository.save(teacher));
    }

    public void delete(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }

    public Teacher getTeacherOrThrow(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + id));
    }

    public TeacherResponse toResponse(Teacher t) {
        return new TeacherResponse(t.getId(), t.getFirstName(), t.getLastName(),
                t.getEmail(), t.getDateOfBirth(), t.getPosition());
    }
}
