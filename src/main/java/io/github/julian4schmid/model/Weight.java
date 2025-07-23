package io.github.julian4schmid.model;

public class Weight {
    private String month;
    private double weight;

    public Weight(String month, double weight) {
        this.month = month;
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public String getMonth() {
        return month;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f)", month, weight);
    }
}
