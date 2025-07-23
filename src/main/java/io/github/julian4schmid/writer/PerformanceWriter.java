package io.github.julian4schmid.writer;

import io.github.julian4schmid.loader.DataLoader;
import io.github.julian4schmid.model.Weight;
import io.github.julian4schmid.model.Performance;
import io.github.julian4schmid.model.Player;

import io.github.julian4schmid.util.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class PerformanceWriter {
    public static void writePerformance(Map<String, Player> playerMap, int numberOfMonths) {
        List<Player> playerList = new ArrayList<>(playerMap.values());
        playerList.sort(Comparator.comparingDouble(Player::getAveragePerformance).reversed());

        // Create Excel file
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Spieler");

        // Header row
        List<String> headers = new ArrayList<>(List.of("Name", "Tag", "Performance"));
        List<String> months = DateUtil.getMonths(numberOfMonths);
        List<Weight> weightList = DataLoader.calculateWeigths(numberOfMonths);
        Map<String, Weight> weightMap = new HashMap<>();
        for (Weight weight : weightList) {
            headers.add(weight.toString());
            weightMap.put(weight.getMonth(), weight);
        }


        Row headerRow = sheet.createRow(0);
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            headerRow.createCell(i).setCellValue(header);
            headerMap.put(header, i);
        }

        // visualize performance
        CellStyle darkGreen = createColoredStyle(workbook, IndexedColors.DARK_GREEN, true);
        CellStyle lightGreen = createColoredStyle(workbook, IndexedColors.LIGHT_GREEN, false);
        CellStyle yellow = createColoredStyle(workbook, IndexedColors.YELLOW, false);
        CellStyle orange = createColoredStyle(workbook, IndexedColors.ORANGE, false);
        CellStyle red = createColoredStyle(workbook, IndexedColors.RED, true);


        // Data rows
        int rowIndex = 1;
        for (Player player : playerList) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(headerMap.get("Name")).setCellValue(player.getName());
            row.createCell(headerMap.get("Tag")).setCellValue(player.getTag());
            double averagePerformance = player.getAveragePerformance();
            Cell cell = row.createCell(headerMap.get("Performance"));
            cell.setCellValue(averagePerformance);

            // Apply style
            if (averagePerformance >= 2.8) {
                cell.setCellStyle(darkGreen);
            } else if (averagePerformance >= 2.6) {
                cell.setCellStyle(lightGreen);
            } else if (averagePerformance >= 2.4) {
                cell.setCellStyle(yellow);
            } else if (averagePerformance >= 2.2) {
                cell.setCellStyle(orange);
            } else {
                cell.setCellStyle(red);
            }

            for (Performance p : player.getPerformanceList()) {
                row.createCell(headerMap.get(weightMap.get(p.getMonth()).toString()))
                        .setCellValue(p.getAverageStars());
            }
        }

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save to file
        try (FileOutputStream fos = new FileOutputStream("target/Performance Übersicht.xlsx")) {
            workbook.write(fos);
            workbook.close();
            System.out.println("Excel file created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static CellStyle createColoredStyle(Workbook wb, IndexedColors bgColor, boolean whiteFont) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(bgColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setColor(whiteFont ? IndexedColors.WHITE.getIndex() : IndexedColors.BLACK.getIndex());
        style.setFont(font);

        return style;
    }
}
