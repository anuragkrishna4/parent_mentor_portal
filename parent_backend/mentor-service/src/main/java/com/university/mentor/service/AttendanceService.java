package com.university.mentor.service;

import com.university.mentor.entity.*;
import com.university.mentor.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentAttendanceRepository attendanceRepository;




    public void saveAttendance(String rollNumber, String studentName, String subjectName,
                               Integer totalClasses, Integer absences, Integer present,
                               Double percent, LocalDate recordDate) {


        Subject subject = subjectRepository.findBySubjectName(subjectName)
                .orElseGet(() -> subjectRepository.save(Subject.builder().subjectName(subjectName).build()));


        Student student = studentRepository.findByRollNumber(rollNumber)
                .orElseGet(() -> studentRepository.save(Student.builder().rollNumber(rollNumber).name(studentName).build()));


        StudentSubject studentSubject = studentSubjectRepository
                .findByStudent_RollNumberAndSubject_Id(rollNumber, subject.getId())
                .orElseGet(() -> studentSubjectRepository.save(StudentSubject.builder().student(student).subject(subject).build()));


        StudentAttendance attendance = attendanceRepository.findByStudentSubject(studentSubject)
                .orElse(new StudentAttendance());

        attendance.setStudentSubject(studentSubject);
        attendance.setRollNumber(rollNumber);
        attendance.setSubjectName(subjectName);
        attendance.setTotalClasses(totalClasses);
        attendance.setAbsences(absences);
        attendance.setPresent(present);
        attendance.setAttendancePercent(percent);

        attendance.setRecordDate(recordDate);

        attendanceRepository.save(attendance);
    }



    public String assignSubjectToStudent(Long subjectId, String rollNumber) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Student student = studentRepository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        boolean exists = studentSubjectRepository.existsByStudent_RollNumberAndSubject_Id(rollNumber, subjectId);

        if (exists) {
            return "Subject already mapped to this student";
        }

        StudentSubject mapping = StudentSubject.builder()
                .student(student)
                .subject(subject)
                .build();

        studentSubjectRepository.save(mapping);

        return "Subject '" + subject.getSubjectName() + "' assigned to " + student.getName();
    }
}