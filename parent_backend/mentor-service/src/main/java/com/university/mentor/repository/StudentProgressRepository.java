package com.university.mentor.repository;

import com.university.mentor.entity.StudentProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentProgressRepository
        extends JpaRepository<StudentProgress, Long> {


    List<StudentProgress> findByMentorUsername(String mentorUsername);

    Optional<StudentProgress> findByRollNumber(String rollNumber);

}
