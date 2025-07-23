package io.github.julian4schmid.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Performance {
    private final int attacks;
    private final int stars;
    private final int dips;
    private final double weight;
    private final double averageStars;

    public Performance(int attacks, int stars, int dips, double weight) {
        this.weight = weight;
        this.dips = dips;
        this.stars = stars;
        this.attacks = attacks;
        this.averageStars = 1.0 * (stars - 3 * dips) / (attacks - dips);
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
}
