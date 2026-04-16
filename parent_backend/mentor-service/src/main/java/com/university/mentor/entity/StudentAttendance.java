package com.university.mentor.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_subject_id")
    private StudentSubject studentSubject;


    private String rollNumber;
    private String subjectName;
    private Integer totalClasses;
    private Integer absences;
    private Integer present;
    private Double attendancePercent;

    private LocalDate recordDate;
}
