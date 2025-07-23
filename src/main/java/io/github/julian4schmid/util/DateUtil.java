package io.github.julian4schmid.util;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateUtil {
    public static List<String> getMonths(int n) {
        List<String> months = new ArrayList<>();

        LocalDate today = LocalDate.now();
        for (int i = 0; i < n; i++) {
            LocalDate date = today.minusMonths(i);
            months.add(date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN));
        }

        return months;
    }
}
