package com.university.parent.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentCsvDto {

    @CsvBindByName(column = "Roll Number")
    private String rollNumber;

    @CsvBindByName(column = "Full Name")
    private String studentName;

    @CsvBindByName(column = "Parent Name")
    private String parentName;

    @CsvBindByName(column = "Phone Number")
    private String phoneNumber;
}