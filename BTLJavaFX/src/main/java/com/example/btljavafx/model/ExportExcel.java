package com.example.btljavafx.model;

import com.example.btljavafx.utils.dao.NhanVienDAO;
import com.example.btljavafx.utils.dao.SettingsDAO;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

//import static com.example.btljavafx.controller.AdminController.getAbsPath;

public class ExportExcel {
    public ExportExcel(String month, Map<String, Pair<String, Double>> employees) {
        SettingsDAO settings = new SettingsDAO();
        double wage = Double.parseDouble(settings.get("PAYMENT_WAGE"));
        String path = settings.get("REPORT_PATH");
        File file = new File(path);

//        double totalOfIncome = Double.parseDouble(income);
        System.out.println(wage);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("File mới được tạo: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            System.out.println("File đã tồn tại, ghi đè lên file này");
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo");
        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Báo cáo doanh thu tháng " + month);
//        Row row2 = sheet.createRow(rowNum++);
//        row2.createCell(0).setCellValue("Doanh số bán hàng:" );
//        row2.createCell(1).setCellValue(totalOfIncome);

        Row row3 = sheet.createRow(rowNum++);
        row3.createCell(0).setCellValue("Lương/1 ngày");
        row3.createCell(1).setCellValue(wage);
        Row row4 = sheet.createRow(rowNum++);
        row4.createCell(0).setCellValue("");
        Row row5 = sheet.createRow(rowNum++);
        row5.createCell(0).setCellValue("Báo cáo số giờ làm việc");
        Row colRow = sheet.createRow(rowNum++);
        colRow.createCell(0).setCellValue("ID Nhân Viên");
        colRow.createCell(1).setCellValue("Tên nhân viên");
        colRow.createCell(2).setCellValue("Số ngày làm");
        colRow.createCell(3).setCellValue("Thuế thu nhập 10%");
//        colRow.createCell(3).setCellValue("Thưởng");
        colRow.createCell(4).setCellValue("Tổng nhận");
        colRow.createCell(5).setCellValue("Tên ngân hàng");
        colRow.createCell(6).setCellValue("STK");
        NhanVienDAO nhanVienDAO = new NhanVienDAO();
        for (Map.Entry<String, Pair<String, Double>> entry : employees.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            Pair<String, Double> nameAndCnt = entry.getValue();
            String name = nameAndCnt.getKey();
            double cnt = nameAndCnt.getValue();




            row.createCell(1).setCellValue(name);
            row.createCell(2).setCellValue(cnt);


            row.createCell(3).setCellValue(cnt * 0.1 * wage);
            row.createCell(4).setCellValue(cnt * wage - cnt * wage * 0.1);
            row.createCell(5).setCellValue(nhanVienDAO.getById(entry.getKey()).getBankName());
            row.createCell(6).setCellValue(nhanVienDAO.getById(entry.getKey()).getNumAccount());

        }
//        for (Map.Entry<String, Double> entry : employees.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }


        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Ghi file
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
            workbook.close();
            System.out.println("Xuất Excel thành công!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Test
//    public static void main(String[] args) {
//        Map<String, Double> employees = Map.of(
//                "user1", 400000.0,
//                "user2", 320000.0,
//                "user3", 450000.0
//        );

//        ExportExcel export = new ExportExcel();
//        export.exportExcel("10", "10000", employees);
//    }
}
