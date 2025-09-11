package io.github.julian4schmid;

import io.github.julian4schmid.loader.DataLoader;
import io.github.julian4schmid.model.Player;
import io.github.julian4schmid.service.PerformanceCalculator;
import io.github.julian4schmid.writer.PerformanceWriter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // data
        // -------------------------------------------------------------------------------------------------------------
        int numberOfMonths = 4;
        List<String> sheetnames = Arrays.asList(
                "Therapiesitzung (#2RQRCCVJ0)",
                "Kings & Queens (#2YUVGPR9U)",
                "Kings & Queens2 (#2Q0RRLV08)",
                "Kings & Queens3 (#2YRC8RU8V)",
                "Kings & Queens4 (#2JLU808R0)",
                "KQ CWL (#2J98RCYYC)",
                "MAHATMA GÖNNDIR (#2LVJ0L2Y0)",
                "Gandhis Erben (#2R8QCRV0P)",
                "Rhön United (#2LQYRJUP9)",
                "Rhön City (#2R2JLU8PR)",
                "Rhön Ultras (#2JPVCYULV)",
                "Ostsee United (#2RQ80YGL8)"
        );
        String inputFilenameFormat = "Royal United Ducks [CWL Stats] %s.xlsx";
        // -------------------------------------------------------------------------------------------------------------

        Map<String, Player> playerMap = DataLoader.loadPlayerData(numberOfMonths, inputFilenameFormat, sheetnames);
        PerformanceCalculator.calculatePerformance(playerMap, numberOfMonths, true);
        PerformanceCalculator.calculatePerformance(playerMap, numberOfMonths);
        Map<String, List<Player>> rosterMap = DataLoader.loadRosterData(playerMap);
        PerformanceWriter.writePerformance(playerMap, numberOfMonths, rosterMap);
    }
}