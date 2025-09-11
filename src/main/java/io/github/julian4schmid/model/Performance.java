package io.github.julian4schmid.model;

import io.github.julian4schmid.util.MathUtil;

public class Performance {
    private final int attacks;
    private final int stars;
    private final int dips;
    private double weightDouble;
    private final String month;
    private final double averageStars;

    public Performance(int attacks, int stars, int dips, String month) {
        this.dips = dips;
        this.stars = stars;
        this.attacks = attacks;
        this.month = month;
        this.weightDouble = 0;
        this.averageStars = MathUtil.roundWithDecimals(1.0 * (stars - 3 * dips) / (attacks - dips), 2);
    }

    public double getAverageStars() {
        return averageStars;
    }

    public double getWeightDouble() {
        return weightDouble;
    }

    public String getMonth() {
        return month;
    }

    public void setWeightDouble(double weightDouble) {
        this.weightDouble = weightDouble;
    }

    @Override
    public String toString() {
        return "Performance{" +
                "attacks=" + attacks +
                ", stars=" + stars +
                ", dips=" + dips +
                ", weight=" + weightDouble +
                ", month='" + month + '\'' +
                ", averageStars=" + averageStars +
                '}';
    }
}
