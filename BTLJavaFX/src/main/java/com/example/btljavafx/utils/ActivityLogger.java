package com.example.btljavafx.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLogger {
    private static final String LOG_FILE = "log.xlsx";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void writeLog(String user, String action) {
        try {
            Workbook workbook;
            Sheet sheet;

            Path logPath = Paths.get(LOG_FILE);

            if (Files.exists(logPath)) {
                // Mở file có sẵn
                try (InputStream in = Files.newInputStream(logPath)) {
                    workbook = new XSSFWorkbook(in);
                }
                sheet = workbook.getSheetAt(0);
            } else {
                // Tạo file mới
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Logs");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Time");
                header.createCell(1).setCellValue("User");
                header.createCell(2).setCellValue("Action");
            }

            int lastRow = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRow + 1);
            row.createCell(0).setCellValue(LocalDateTime.now().format(formatter));
            row.createCell(1).setCellValue(user);
            row.createCell(2).setCellValue(action);

            try (OutputStream out = Files.newOutputStream(logPath)) {
                workbook.write(out);
            }
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
