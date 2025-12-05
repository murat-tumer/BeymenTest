package com.beymen.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelReader {
    private static final Logger logger = LogManager.getLogger(ExcelReader.class);
    private Workbook workbook;
    private Sheet sheet;

    public ExcelReader(String filePath) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                inputStream = new FileInputStream(filePath);
            }
            workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
            logger.info("Excel file loaded: " + filePath);
        } catch (IOException e) {
            logger.error("Error loading Excel file: " + e.getMessage());
            throw new RuntimeException("Failed to load Excel file: " + filePath, e);
        }
    }

    public String readCell(int column, int row) {
        try {
            Row dataRow = sheet.getRow(row);
            if (dataRow == null) {
                logger.warn("Row " + row + " is null");
                return "";
            }
            Cell cell = dataRow.getCell(column);
            if (cell == null) {
                logger.warn("Cell at column " + column + ", row " + row + " is null");
                return "";
            }
            String value = getCellValueAsString(cell);
            logger.info("Read cell [" + column + "," + row + "]: " + value);
            return value;
        } catch (Exception e) {
            logger.error("Error reading cell: " + e.getMessage());
            return "";
        }
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
                logger.info("Excel workbook closed");
            }
        } catch (IOException e) {
            logger.error("Error closing workbook: " + e.getMessage());
        }
    }
}
