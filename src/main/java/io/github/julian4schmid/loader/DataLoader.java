package io.github.julian4schmid.loader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.Locale;

import io.github.julian4schmid.model.*;


public class DataLoader {
    public static void loadData() {
        int numberOfMonths = 3;
        List<String> sheetnames = Arrays.asList(
                "MAHATMA GÖNNDIR (#2LVJ0L2Y0)",
                "Therapiesitzung (#2RQRCCVJ0)",
                "Kings & Queens2 (#2Q0RRLV08)",
                "Kings & Queens3 (#2YRC8RU8V)",
                "Gandhis Erben (#2R8QCRV0P)",
                "Kings & Queens (#2YUVGPR9U)",
                "Rhön City (#2R2JLU8PR)",
                "Rhön United (#2LQYRJUP9)"
        );

        Map<String, Player> playerMap = new HashMap<>();
        Map<String, Integer> headerMap = new HashMap<>();

        LocalDate today = LocalDate.now();
        for (int i = 0; i < numberOfMonths; i++) {
            LocalDate date = today.minusMonths(i);
            String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN);
            String filename = String.format("Royale United Ducks [CWL Stats] %s.xlsx", month);

            // weight: latest data more important
            double weight = 2 - i * (1.0 / (numberOfMonths - 1));

            try (FileInputStream fis = new FileInputStream(new File(filename));
                 Workbook workbook = new XSSFWorkbook(fis)) {

                for (String sheetname : sheetnames) {
                    Sheet sheet = workbook.getSheet(sheetname);

                    // no cwl this season
                    if (sheet == null) continue;

                    Iterator<Row> rowIterator = sheet.iterator();
                    // last row reached
                    if (!rowIterator.hasNext()) continue;

                    // header
                    Row headerRow = rowIterator.next();
                    if (headerMap.isEmpty()) {
                        for (Cell cell : headerRow) {
                            headerMap.put(cell.getStringCellValue(), cell.getColumnIndex());
                        }
                    }

                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        String name = getCellValue(row, headerMap.get("Name"));
                        String tag = getCellValue(row, headerMap.get("Tag"));
                        int level = getNumericCellValue(row, headerMap.get("Town Hall"));
                        int attacks = getNumericCellValue(row, headerMap.get("Number of Attacks"));
                        int stars = getNumericCellValue(row, headerMap.get("Total Stars"));
                        int dips = getNumericCellValue(row, headerMap.get("Lower TH Hits (Dips)"));

                        // only for level 17
                        if (level == 17 && dips < attacks) {
                            Player player = playerMap.getOrDefault(tag, new Player(name, tag));
                            player.getPerformanceList().add(new Performance(attacks, stars, dips, weight));
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        calculatePerformance(playerMap);
    }


    public static void calculatePerformance(Map<String, Player> playerMap) {
        for (Player player : playerMap.values()) {
            double weightedStars = 0;
            double weights = 0;
            for (Performance p : player.getPerformanceList()) {
                weightedStars += p.getAverageStars() * p.getWeight();
                weights += p.getWeight();
            }
            double averagePerformance = BigDecimal.valueOf(weightedStars / weights)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            player.setAveragePerformance(averagePerformance);
        }
    }


    private static String getCellValue(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : cell.toString();
    }

    private static int getNumericCellValue(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return 0;
        return cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : 0;
    }
}