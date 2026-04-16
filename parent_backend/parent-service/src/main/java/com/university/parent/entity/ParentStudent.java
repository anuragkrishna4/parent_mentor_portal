package com.university.parent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parent_student")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long parentId;

    private String studentRollNumber;

    private String parentName;
}