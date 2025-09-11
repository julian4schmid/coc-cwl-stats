package io.github.julian4schmid.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final String tag;
    private final List<Performance> performanceList;
    private double averagePerformance;
    private int previousRank;
    private int newRank;

    public Player(String name, String tag) {
        this.name = name;
        this.tag = tag;
        this.performanceList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public List<Performance> getPerformanceList() {
        return performanceList;
    }

    public double getAveragePerformance() {
        return averagePerformance;
    }

    public void setAveragePerformance(double averagePerformance) {
        this.averagePerformance = averagePerformance;
    }

    public int getNewRank() {
        return newRank;
    }

    public int getPreviousRank() {
        return previousRank;
    }

    public void setNewRank(int newRank) {
        this.newRank = newRank;
    }

    public void setPreviousRank(int previousRank) {
        this.previousRank = previousRank;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", tag='" + tag + '\'' +
                ", performanceList=" + performanceList +
                ", averagePerformance=" + averagePerformance +
                '}';
    }

}
