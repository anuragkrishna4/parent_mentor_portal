package com.university.parent.repository;

import com.university.parent.entity.ParentStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParentStudentRepository extends JpaRepository<ParentStudent, Long> {

    List<ParentStudent> findByParentId(Long parentId);

    boolean existsByParentIdAndStudentRollNumber(Long parentId, String studentRollNumber);

    List<ParentStudent> findByStudentRollNumber(String studentRollNumber);
}