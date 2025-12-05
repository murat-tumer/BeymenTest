package com.beymen.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class TestDataGenerator {

    public static void main(String[] args) {
        createTestDataExcel();
    }

    public static void createTestDataExcel() {
        String filePath = "src/test/resources/testdata.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TestData");

            Row row = sheet.createRow(0);

            Cell cell1 = row.createCell(0);
            cell1.setCellValue("şort");

            Cell cell2 = row.createCell(1);
            cell2.setCellValue("gömlek");

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("Test data Excel file created successfully at: " + filePath);
            }

        } catch (IOException e) {
            System.err.println("Error creating Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
