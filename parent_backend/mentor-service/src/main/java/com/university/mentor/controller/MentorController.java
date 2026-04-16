package com.university.mentor.controller;

import com.university.mentor.entity.Student;
import com.university.mentor.entity.StudentProgress;
import com.university.mentor.entity.Subject;
import com.university.mentor.dto.SubjectAssignmentRequest;
import com.university.mentor.repository.StudentProgressRepository;
import com.university.mentor.repository.StudentRepository;
import com.university.mentor.repository.SubjectRepository;
import com.university.mentor.service.AttendanceService;
import com.university.mentor.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/mentor")
@RequiredArgsConstructor
public class MentorController {

    private final StudentProgressRepository repository;
    private final FileUploadService fileUploadService;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceService attendanceService;

    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/add")
    public String addProgress(@RequestBody StudentProgress progress,
                              Authentication authentication) {

        progress.setMentorUsername(authentication.getName());
        repository.save(progress);

        return "Progress added by " + authentication.getName();
    }

    @PreAuthorize("hasAnyRole('ADMIN' , 'MENTOR')")

    @GetMapping("/all")
    public List<StudentProgress> getAll() {
        return repository.findAll();
    }



    @PreAuthorize("hasRole('MENTOR')")
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             Authentication authentication) throws Exception {

        String username = authentication.getName();
        fileUploadService.processFile(file, username);

        return "File uploaded successfully by " + username;
    }


    @PreAuthorize("hasRole('MENTOR')")
    @GetMapping("/my")
    public List<StudentProgress> getMyStudents(Authentication authentication) {

        return repository.findByMentorUsername(authentication.getName());
    }

    @GetMapping("/progress/{rollNumber}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN', 'PARENT')")
    public StudentProgress getProgressByRollNumber(@PathVariable String rollNumber) {
        return repository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException("Progress not found"));
    }

    @PostMapping("/student/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/subject/add")
    public Subject addSubject(@RequestBody Subject subject) {
        return subjectRepository.save(subject);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<String> assignSubject(@RequestBody SubjectAssignmentRequest request) {

        String result = attendanceService.assignSubjectToStudent(
                request.getSubjectId(),
                request.getRollNumber()
        );
        return ResponseEntity.ok(result);
    }


}
