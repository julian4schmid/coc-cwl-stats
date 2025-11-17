package io.github.julian4schmid.service;

import io.github.julian4schmid.model.Performance;
import io.github.julian4schmid.model.Player;
import io.github.julian4schmid.model.PlayerComparator;
import io.github.julian4schmid.model.Weight;
import io.github.julian4schmid.util.DateUtil;
import io.github.julian4schmid.util.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerformanceCalculator {

    public static void calculatePerformance(Map<String, Player> playerMap, int numberOfMonths, boolean previous) {
        List<Weight> weightList = PerformanceCalculator.calculateWeights(numberOfMonths);
        if (previous) {
            weightList = PerformanceCalculator.calculateWeights(1, numberOfMonths + 1);
        }

        for (Player player : playerMap.values()) {
            double weightedStars = 0;
            double weights = 0;
            for (Performance perf : player.getPerformanceList()) {
                for (Weight w : weightList) {
                    if (w.getMonth().equals(perf.getMonth())) {
                        perf.setWeightDouble(w.getWeight());
                    }
                }
                weightedStars += perf.getAverageStars() * perf.getWeightDouble();
                weights += perf.getWeightDouble();
            }

            double averagePerformance = 0;
            if (weights > 0) {
                averagePerformance = MathUtil.roundWithDecimals(weightedStars / weights, 2);
            }
            player.setAveragePerformance(averagePerformance);

            // reset weights if we just calculated the ranks of the previuos month
            if (previous) {
                resetWeights(player.getPerformanceList());
            }
        }

        calculateRanks(playerMap, previous);
    }

    public static void calculateRanks(Map<String, Player> playerMap, boolean previous) {
        List<Player> playerList = new ArrayList<>(playerMap.values());
        playerList.sort(new PlayerComparator());
        int rank = 1;
        for (Player p : playerList) {
            if (p.getAveragePerformance() == 0) {
                break;
            }
            if (previous) {
                p.setPreviousRank(rank++);
            } else {
                p.setNewRank(rank++);
            }
        }
    }

    public static List<Weight> calculateWeights(int end) {
        return calculateWeights(0, end);
    }

    public static List<Weight> calculateWeights(int start, int end) {
        List<String> months = DateUtil.getMonths(start, end);
        List<Weight> weightList = new ArrayList<>();

        for (int i = 0; i < end - start; i++) {
            String month = months.get(i);
            // weight: latest data more important
            double weight = MathUtil.roundWithDecimals(2 - i * (1.0 / (end - start - 1)), 2);
            weightList.add(new Weight(month, weight));
        }
        return weightList;
    }

    public static void resetWeights(List<Performance> performanceList) {
        for (Performance perf: performanceList) {
            perf.setWeightDouble(0);
        }
    }

}
