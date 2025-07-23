package io.github.julian4schmid.model;

import io.github.julian4schmid.util.MathUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Performance {
    private final int attacks;
    private final int stars;
    private final int dips;
    private final double weight;
    private final String month;
    private final double averageStars;

    public Performance(int attacks, int stars, int dips, double weight, String month) {
        this.weight = weight;
        this.dips = dips;
        this.stars = stars;
        this.attacks = attacks;
        this.month = month;
        this.averageStars = MathUtil.roundWithDecimals(1.0 * (stars - 3 * dips) / (attacks - dips), 2);
    }

    public double getAverageStars() {
        return averageStars;
    }

    public double getWeight() {
        return weight;
    }

    public int getDips() {
        return dips;
    }

    public int getStars() {
        return stars;
    }

    public int getAttacks() {
        return attacks;
    }

    public String getMonth() {
        return month;
    }

    @Override
    public String toString() {
        return "Performance{" +
                "attacks=" + attacks +
                ", stars=" + stars +
                ", dips=" + dips +
                ", weight=" + weight +
                ", month='" + month + '\'' +
                ", averageStars=" + averageStars +
                '}';
    }
}
