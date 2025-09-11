package io.github.julian4schmid.model;

import java.util.Comparator;

public class PlayerComparator implements Comparator<Player> {
    @Override
    public int compare(Player a, Player b) {
        // averagePerformance
        int x = Double.compare(b.getAveragePerformance(), a.getAveragePerformance());
        if (x != 0 || b.getPerformanceList().isEmpty() || a.getPerformanceList().isEmpty()) {
            return x;
        }

        // most recent cwl
        x = Double.compare(b.getPerformanceList().getFirst().getWeightDouble(),
                a.getPerformanceList().getFirst().getWeightDouble());
        if (x != 0) {
            return x;
        }

        // recent performance
        x = Double.compare(b.getPerformanceList().getFirst().getAverageStars(),
                a.getPerformanceList().getFirst().getAverageStars());
        if (x != 0) {
            return x;
        }

        // number of recorded performances
        x = Integer.compare(b.getPerformanceList().size(), a.getPerformanceList().size());
        if (x != 0) {
            return x;
        }

        // names
        return a.getName().compareTo(b.getName());
    }
}
