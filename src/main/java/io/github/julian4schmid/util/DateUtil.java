package io.github.julian4schmid.util;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateUtil {
    // cwl data only available after the 8th
    private static final int dayOffset = 8;

    public static List<String> getMonths(int end) {
        return getMonths(0, end);
    }

    public static List<String> getMonths(int start, int end) {
        List<String> months = new ArrayList<>();

        LocalDate today = LocalDate.now();

        today = today.minusDays(dayOffset);
        for (int i = start; i < end + 1; i++) {
            LocalDate date = today.minusMonths(i);
            months.add(date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN));
        }

        return months;
    }

    public static String getCurrentMonth() {
        LocalDate today = LocalDate.now();
        today = today.minusDays(dayOffset);
        return today.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN);
    }
}
