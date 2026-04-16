package com.university.mentor.service;

import com.opencsv.CSVReader;
import com.university.mentor.entity.StudentProgress;
import com.university.mentor.repository.StudentProgressRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final StudentProgressRepository repository;

    public void processFile(MultipartFile file, String mentorUsername) throws Exception {

        String fileName = file.getOriginalFilename();

        if (fileName != null && fileName.endsWith(".csv")) {
            processCSV(file, mentorUsername);
        } else if (fileName != null && fileName.endsWith(".xlsx")) {
            processExcel(file, mentorUsername);
        } else {
            throw new RuntimeException("Unsupported file format");
        }
    }


    private void processCSV(MultipartFile file, String mentorUsername) throws Exception {

        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
        List<String[]> records = reader.readAll();

        for (int i = 1; i < records.size(); i++) {
            String[] row = records.get(i);

            StudentProgress progress = StudentProgress.builder()
                    .rollNumber(row[0].trim())
                    .studentName(row[1].trim())
//                    .subject(row[2].trim())
                    .cgpa(Double.parseDouble(row[2].trim()))
                    .backlog(Integer.parseInt(row[3].trim()))
                    .remarks(row[4].trim())
                    .mentorUsername(mentorUsername)
                    .build();

            repository.save(progress);
        }

        reader.close();
    }


    private void processExcel(MultipartFile file, String mentorUsername) throws Exception {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            StudentProgress progress = StudentProgress.builder()
                    .rollNumber(row.getCell(0).getStringCellValue())
                    .studentName(row.getCell(1).getStringCellValue())
//                    .subject(row.getCell(2).getStringCellValue())
                    .cgpa(row.getCell(2).getNumericCellValue())
                    .backlog((int) row.getCell(3).getNumericCellValue())
                    .remarks(row.getCell(4).getStringCellValue())
                    .mentorUsername(mentorUsername)
                    .build();

            repository.save(progress);
        }

        workbook.close();
    }
}
