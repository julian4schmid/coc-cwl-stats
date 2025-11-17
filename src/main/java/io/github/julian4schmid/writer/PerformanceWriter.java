package io.github.julian4schmid.writer;

import io.github.julian4schmid.model.Weight;
import io.github.julian4schmid.model.Performance;
import io.github.julian4schmid.model.Player;
import io.github.julian4schmid.model.PlayerComparator;
import io.github.julian4schmid.service.PerformanceCalculator;
import io.github.julian4schmid.util.DateUtil;
import io.github.julian4schmid.util.MathUtil;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class PerformanceWriter {
    public static void writePerformance(Map<String, Player> playerMap, int numberOfMonths,
                                        Map<String, List<Player>> rosterMap) {
        List<Player> playerList = new ArrayList<>(playerMap.values());
        playerList.sort(new PlayerComparator());

        // Create Excel file
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Spieler");

        // Header row
        List<String> headers = new ArrayList<>(List.of("Delta", "Rang", "Name", "Tag", "Performance"));
        List<Weight> weightList = PerformanceCalculator.calculateWeights(numberOfMonths);
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
        Map<String, CellStyle> styleMap = createColoredStyles(workbook);

        // Data rows
        int rowIndex = 1;
        for (Player player : playerList) {
            double averagePerformance = player.getAveragePerformance();
            if (averagePerformance > 0) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(headerMap.get("Rang")).setCellValue(player.getNewRank());

                int rankChange = player.getPreviousRank() - player.getNewRank();
                String arrow = rankChange < 0 ? "↘ "
                        : rankChange == 0 ? "→ "
                        : "↗ ";
                if (player.getPreviousRank() > 0) {
                    row.createCell(headerMap.get("Delta")).setCellValue(arrow + rankChange);
                } else {
                    row.createCell(headerMap.get("Delta")).setCellValue("↑ ");
                }

                row.createCell(headerMap.get("Name")).setCellValue(player.getName());
                row.createCell(headerMap.get("Tag")).setCellValue(player.getTag());
                Cell cell = row.createCell(headerMap.get("Performance"));
                cell.setCellValue(averagePerformance);

                // Apply style
                colorCell(cell, averagePerformance, styleMap);

                for (Performance p : player.getPerformanceList()) {
                    if (weightMap.containsKey(p.getMonth())) {
                        row.createCell(headerMap.get(weightMap.get(p.getMonth()).toString()))
                                .setCellValue(p.getAverageStars());
                    }
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < 5 + numberOfMonths; i++) {
            sheet.autoSizeColumn(i);
        }

        // roster performance
        writePerformanceToRoster(workbook, rosterMap);

        // Save to file
        String name = String.format("target/CWL Power Ranking %s.xlsx", DateUtil.getMonths(1).getFirst());
        try (FileOutputStream fos = new FileOutputStream(name)) {
            workbook.write(fos);
            workbook.close();
            System.out.println("Excel file created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writePerformanceToRoster(Workbook wb, Map<String, List<Player>> rosterMap) {
        Sheet sheet = wb.createSheet("Roster");
        List<String> headers = new ArrayList<>(List.of("Clan", "Name", "Tag", "Performance", "Letzte CWL"));

        Map<String, CellStyle> styleMap = createColoredStyles(wb);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            headerRow.createCell(i).setCellValue(header);
        }

        int rowIndex = 1;
        for (String clan : rosterMap.keySet()) {
            rowIndex++;

            List<Player> playerList = rosterMap.get(clan);
            playerList.sort(new PlayerComparator());
            double sumPerformance = 0;
            int countPerformance = 0;
            double sumRecentPerformance = 0;
            int countRecentPerformance = 0;
            for (Player player : playerList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(clan);
                row.createCell(1).setCellValue(player.getName());
                row.createCell(2).setCellValue(player.getTag());

                // average Performance
                Cell cell = row.createCell(3);
                cell.setCellValue(player.getAveragePerformance());
                colorCell(cell, player.getAveragePerformance(), styleMap);
                if (player.getAveragePerformance() >= 0.1) {
                    sumPerformance += player.getAveragePerformance();
                    countPerformance++;
                }

                // performance of most recent cwl
                if (!player.getPerformanceList().isEmpty() &&
                        player.getPerformanceList().getFirst().getMonth().equals(DateUtil.getCurrentMonth())) {
                    cell = row.createCell(4);
                    double recentPerformance = player.getPerformanceList().getFirst().getAverageStars();
                    cell.setCellValue(recentPerformance);
                    //colorCell(cell, recentPerformance, styleMap);
                    if (recentPerformance >= 0.1) {
                        sumRecentPerformance += recentPerformance;
                        countRecentPerformance++;
                    }
                }
            }
            if (countPerformance > 0) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(1).setCellValue("Durchschnitt (17er)");
                Cell cell = row.createCell(3);
                double avgRoster = MathUtil.roundWithDecimals(sumPerformance / countPerformance, 2);
                cell.setCellValue(avgRoster);
                colorCell(cell, avgRoster, styleMap);

                cell = row.createCell(4);
                if (countRecentPerformance > 0) {
                    avgRoster = MathUtil.roundWithDecimals(sumRecentPerformance / countRecentPerformance, 2);
                    cell.setCellValue(avgRoster);
                    colorCell(cell, avgRoster, styleMap);
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
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

    private static Map<String, CellStyle> createColoredStyles(Workbook wb) {
        Map<String, CellStyle> styleMap = new HashMap<>();
        styleMap.put("darkGreen", createColoredStyle(wb, IndexedColors.DARK_GREEN, true));
        styleMap.put("lightGreen", createColoredStyle(wb, IndexedColors.LIGHT_GREEN, false));
        styleMap.put("lightOrange", createColoredStyle(wb, IndexedColors.LIGHT_ORANGE, false));
        styleMap.put("orange", createColoredStyle(wb, IndexedColors.ORANGE, false));
        styleMap.put("red", createColoredStyle(wb, IndexedColors.RED, true));
        return styleMap;
    }

    private static void colorCell(Cell cell, double val, Map<String, CellStyle> styleMap) {
        if (val >= 2.8) {
            cell.setCellStyle(styleMap.get("darkGreen"));
        } else if (val >= 2.6) {
            cell.setCellStyle(styleMap.get("lightGreen"));
        } else if (val >= 2.4) {
            cell.setCellStyle(styleMap.get("lightOrange"));
        } else if (val >= 2.2) {
            cell.setCellStyle(styleMap.get("orange"));
        } else if (val >= 0.1) {
            cell.setCellStyle(styleMap.get("red"));
        }
    }
}
