package io.github.julian4schmid.loader;

import io.github.julian4schmid.util.DateUtil;
import io.github.julian4schmid.util.MathUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

import java.util.*;


import io.github.julian4schmid.model.*;


public class DataLoader {
    public static Map<String, Player> loadPlayerData(int numberOfMonths, String filenameFormat, List<String> sheetNames) {
        Map<String, Player> playerMap = new HashMap<>();
        Map<String, Integer> headerMap = new HashMap<>();

        List<String> months = DateUtil.getMonths(numberOfMonths);
        List<Weight> weightList = calculateWeights(numberOfMonths);
        for (int i = 0; i < numberOfMonths; i++) {
            String month = months.get(i);
            String filename = String.format(filenameFormat, month);

            double weight = weightList.get(i).getWeight();

            try (InputStream is = DataLoader.class.getClassLoader().getResourceAsStream(filename)) {
                if (is == null) {
                    throw new FileNotFoundException("Resource not found: " + filename);
                }

                Workbook workbook = new XSSFWorkbook(is);
                for (String sheetName : sheetNames) {
                    Sheet sheet = workbook.getSheet(sheetName);

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
                        int attacks = getNumericCellValue(row, headerMap.get("Wars Participated"));
                        int stars = getNumericCellValue(row, headerMap.get("Total Stars"));
                        int dips = getNumericCellValue(row, headerMap.get("Lower TH Hits (Dips)"));

                        // only for level 17
                        if (level == 17 && dips < attacks) {
                            playerMap.putIfAbsent(tag, new Player(name, tag));
                            Player player = playerMap.get(tag);
                            player.getPerformanceList().add(new Performance(attacks, stars, dips, weight, month));
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        calculatePerformance(playerMap);
        return playerMap;
    }

    public static Map<String, List<Player>> loadRosterData(Map<String, Player> playerMap) {
        String filename = "Royal United Ducks [Rosters].xlsx";
        String sheetName = "All Members";
        Map<String, List<Player>> rosterMap = new HashMap<>();

        try (InputStream is = DataLoader.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + filename);
            }

            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(sheetName);
            Iterator<Row> rowIterator = sheet.iterator();


            // header
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String name = getCellValue(row, 0);
                String tag = getCellValue(row, 1);
                String clan = getCellValue(row, 3);

                rosterMap.putIfAbsent(clan, new ArrayList<>());
                rosterMap.get(clan).add(playerMap.getOrDefault(tag, new Player(name, tag)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rosterMap;
    }


    public static void calculatePerformance(Map<String, Player> playerMap) {
        for (Player player : playerMap.values()) {
            double weightedStars = 0;
            double weights = 0;
            for (Performance p : player.getPerformanceList()) {
                weightedStars += p.getAverageStars() * p.getWeight();
                weights += p.getWeight();
            }

            double averagePerformance = MathUtil.roundWithDecimals(weightedStars / weights, 2);
            player.setAveragePerformance(averagePerformance);
        }
    }

    public static List<Weight> calculateWeights(int numberOfMonths) {
        List<String> months = DateUtil.getMonths(numberOfMonths);
        List<Weight> monthWeightMap = new ArrayList<>();

        for (int i = 0; i < numberOfMonths; i++) {
            String month = months.get(i);
            // weight: latest data more important
            double weight = MathUtil.roundWithDecimals(2 - i * (1.0 / (numberOfMonths - 1)), 2);
            monthWeightMap.add(new Weight(month, weight));
        }
        return monthWeightMap;
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