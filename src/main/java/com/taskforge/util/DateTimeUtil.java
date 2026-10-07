package com.taskforge.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Standard date-time formatting utilities.
 */
public final class DateTimeUtil {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateTimeUtil() {}

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static String formatDateOnly(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_ONLY_FORMATTER);
    }
}
