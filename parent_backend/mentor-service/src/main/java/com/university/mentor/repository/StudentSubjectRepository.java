package com.university.mentor.repository;

import com.university.mentor.entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    boolean existsByStudent_RollNumberAndSubject_Id(String rollNumber, Long subjectId);

    Optional<StudentSubject> findByStudent_RollNumberAndSubject_Id(String rollNumber, Long subjectId);
}