package com.university.parent.service;

import com.university.parent.client.AuthClient;
import com.university.parent.dto.ParentCsvDto;
import com.university.parent.dto.RegisterRequest;
import com.university.parent.entity.Parent;
import com.university.parent.entity.ParentStudent;
import com.university.parent.entity.Student;
import com.university.parent.repository.ParentRepository;
import com.university.parent.repository.ParentStudentRepository;
import com.university.parent.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParentBulkUploadService {

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final ParentStudentRepository parentStudentRepository;
    private final AuthClient authClient;

    @Transactional
    public void syncParentStudentData(List<ParentCsvDto> csvData) {
        for (ParentCsvDto row : csvData) {

            if (isRowInvalid(row)) {
                log.warn("Skipping empty or invalid CSV row");
                continue;
            }

            Student student = studentRepository.findByRollNumber(row.getRollNumber())
                    .orElseGet(() -> {
                        log.info("Creating new Student: {}", row.getRollNumber());
                        return studentRepository.save(Student.builder()
                                .rollNumber(row.getRollNumber())
                                .name(row.getStudentName())
                                .build());
                    });

            Parent parent = parentRepository.findByUsername(row.getPhoneNumber())
                    .orElseGet(() -> {
                        String phone = row.getPhoneNumber();
                        try {
                            String prefix = (phone.length() >= 3) ? phone.substring(0, 3) : "000";
                            String password = "Parent@" + prefix;

                            authClient.registerParent(new RegisterRequest(phone, password));
                            log.info("Auth Account Created: {} | Pwd: {}", phone, password);
                        } catch (Exception e) {
                            log.warn("Auth registration skipped for {}: (User likely exists)", phone);
                        }

                        return parentRepository.save(Parent.builder()
                                .username(phone)
                                .parentName(row.getParentName())
                                .phone(phone)
                                .studentMappings(new ArrayList<>())
                                .build());
                    });

            boolean mappingExists = parentStudentRepository.existsByParentIdAndStudentRollNumber(
                    parent.getId(),
                    student.getRollNumber()
            );

            if (!mappingExists) {
                parentStudentRepository.save(ParentStudent.builder()
                        .parentId(parent.getId())
                        .studentRollNumber(student.getRollNumber())
                        .parentName(row.getParentName())
                        .build());
                log.info("Successfully linked Student {} to Parent {}", student.getRollNumber(), parent.getUsername());
            }
        }
    }


    private boolean isRowInvalid(ParentCsvDto row) {
        return row.getRollNumber() == null || row.getRollNumber().isBlank() ||
                row.getPhoneNumber() == null || row.getPhoneNumber().isBlank() ||
                row.getStudentName() == null || row.getStudentName().isBlank();
    }
}