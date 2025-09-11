package io.github.julian4schmid.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {
    public static double roundWithDecimals(double val, int decimals) {
        return BigDecimal.valueOf(val)
                .setScale(decimals, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
