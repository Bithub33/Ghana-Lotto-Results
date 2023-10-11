package com.maxstudio.lotto.Utils;

import java.util.Locale;

public class NumberUtil {
    public static String formatNumber(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1_000_000) {
            return String.format(Locale.getDefault(), "%.1fK", number / 1000.0);
        } else if (number < 1_000_000_000) {
            return String.format(Locale.getDefault(), "%.1fM", number / 1_000_000.0);
        } else {
            return String.format(Locale.getDefault(), "%.1fB", number / 1_000_000_000.0);
        }
    }
}

