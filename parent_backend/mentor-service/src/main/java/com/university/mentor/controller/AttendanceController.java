package com.university.mentor.controller;

import com.university.mentor.entity.StudentAttendance;
import com.university.mentor.entity.StudentProgress;
import com.university.mentor.repository.StudentAttendanceRepository;
import com.university.mentor.repository.StudentSubjectRepository;
import com.university.mentor.service.AttendanceFileUploadService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/mentor/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceFileUploadService uploadService;
    private final StudentAttendanceRepository studentAttendanceRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/upload")
    public String uploadAttendance(@RequestParam("file") MultipartFile file) throws Exception {

        uploadService.processFile(file);

        return "Attendance uploaded successfully";
    }

    // This handles the ATTENDANCE list
    @GetMapping("/{rollNumber}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN', 'PARENT')")
    public List<StudentAttendance> getAttendanceByRollNumber(@PathVariable String rollNumber) {
        return studentAttendanceRepository.findByStudentSubject_Student_RollNumber(rollNumber);
    }

    //when to new semester starts
    @PreAuthorize("hasRole('MENTOR')")
    @DeleteMapping("/reset-mappings")
    @Transactional
    public ResponseEntity<String> resetSemesterMappings() {
        try {
            // 1. Delete Attendance records first (The "Child" table)
            studentAttendanceRepository.deleteAll();

            // 2. Delete Student-Subject mappings second (The "Parent" table)
            studentSubjectRepository.deleteAll();

            return ResponseEntity.ok("Semester Reset Successful: Attendance and Mappings cleared.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during reset: " + e.getMessage());
        }
    }


}