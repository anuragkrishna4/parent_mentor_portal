package com.university.mentor.service;

import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceFileUploadService {

    private final AttendanceService attendanceService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    public void processFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.endsWith(".csv")) {
            processCSV(file);
        } else if (fileName != null && fileName.endsWith(".xlsx")) {
            processExcel(file);
        }
    }

    private void processCSV(MultipartFile file) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> records = reader.readAll();

            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);

                // CSV Mapping based on your file:
                // 0:Roll, 1:Name, 2:Class(Subject), 3:Total, 4:Abs, 5:%, 6:Date, 7:Pres
                attendanceService.saveAttendance(
                        row[0].trim(),
                        row[1].trim(),
                        row[2].trim(),
                        (int) Double.parseDouble(row[3].trim()),
                        (int) Double.parseDouble(row[4].trim()),
                        (int) Double.parseDouble(row[7].trim()),
                        Double.parseDouble(row[5].trim()),
                        LocalDate.parse(row[6].trim(), formatter)
                );
            }
        }
    }

    private void processExcel(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                attendanceService.saveAttendance(
                        getCellValue(row.getCell(0)),
                        getCellValue(row.getCell(1)),
                        getCellValue(row.getCell(2)),
                        (int) row.getCell(3).getNumericCellValue(),
                        (int) row.getCell(4).getNumericCellValue(),
                        (int) row.getCell(7).getNumericCellValue(),
                        row.getCell(5).getNumericCellValue(),
                        row.getCell(6).getLocalDateTimeCellValue().toLocalDate()
                );
            }
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.getStringCellValue();
    }
}