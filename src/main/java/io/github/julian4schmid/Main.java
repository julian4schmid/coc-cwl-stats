package io.github.julian4schmid;

import io.github.julian4schmid.loader.DataLoader;
import io.github.julian4schmid.model.Player;
import io.github.julian4schmid.writer.PerformanceWriter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // data
        int numberOfMonths = 4;
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
        String inputFilenameFormat = "Royale United Ducks [CWL Stats] %s.xlsx";


        Map<String, Player> playerMap = DataLoader.loadData(numberOfMonths, inputFilenameFormat, sheetnames);
        PerformanceWriter.writePerformance(playerMap, numberOfMonths);
    }
}