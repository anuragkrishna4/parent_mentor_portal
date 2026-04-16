package com.university.parent.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.university.parent.dto.ParentCsvDto;
import com.university.parent.entity.Parent;
import com.university.parent.entity.Student;
import com.university.parent.repository.StudentRepository;
import com.university.parent.service.ParentBulkUploadService; // Added
import com.university.parent.service.ParentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@RestController
@RequestMapping("/parent")
@RequiredArgsConstructor
@Slf4j
public class ParentController {

    private final ParentService parentService;
    private final StudentRepository studentRepository;
    private final ParentBulkUploadService bulkUploadService;


    @PostMapping("/bulk-upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<String> bulkUpload(@RequestParam("file") MultipartFile file) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<ParentCsvDto> csvToBean = new CsvToBeanBuilder<ParentCsvDto>(reader)
                    .withType(ParentCsvDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<ParentCsvDto> data = csvToBean.parse();
            bulkUploadService.syncParentStudentData(data);
            return ResponseEntity.ok("Processed " + data.size() + " records successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public Parent getProfile(Authentication authentication) {
        return parentService.getParentProfile(authentication.getName());
    }

    @PostMapping("/assign")
    public String assignStudent(@RequestParam String parentUsername,
                                @RequestParam String rollNumber) {
        return parentService.assignStudentToParent(parentUsername, rollNumber);
    }

    @PostMapping("/create")
    public Parent createParent(@RequestBody Parent parent) {
        return parentService.createParent(parent);
    }

    @PostMapping("/student/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('PARENT')")
    public List<Object> getStudentReports(Authentication authentication) {
        return parentService.getDetailedAttendanceReport(authentication.getName());
    }

    @GetMapping("/progress")
    @PreAuthorize("hasRole('PARENT')")
    public List<Object> getStudentProgress(Authentication authentication) {
        return parentService.getStudentsProgress(authentication.getName());
    }
}