package com.university.mentor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rollNumber;

    private String studentName;



    private Double cgpa;

    private int backlog;

    private String remarks;

    private String mentorUsername;
}
