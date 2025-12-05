package com.beymen.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileWriterUtil {
    private static final Logger logger = LogManager.getLogger(FileWriterUtil.class);
    private static final String OUTPUT_FILE = "output/product_info.txt";

    public static void writeProductInfo(String productName, String productPrice) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(OUTPUT_FILE, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);

            writer.println("=".repeat(50));
            writer.println("Tarih: " + timestamp);
            writer.println("Ürün Adı: " + productName);
            writer.println("Fiyat: " + productPrice);
            writer.println("=".repeat(50));
            writer.println();

            logger.info("Product info written to file: " + productName + " - " + productPrice);
        } catch (IOException e) {
            logger.error("Error writing to file: " + e.getMessage());
            throw new RuntimeException("Failed to write product info to file", e);
        }
    }

    public static void clearFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(OUTPUT_FILE, false))) {
            writer.print("");
            logger.info("Output file cleared");
        } catch (IOException e) {
            logger.error("Error clearing file: " + e.getMessage());
        }
    }
}
