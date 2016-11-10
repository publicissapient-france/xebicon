package fr.xebia.xebicon.core.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fr.xebia.xebicon.ui.widget.UIUtils;

public class TimeUtils {

    private static DateFormat sTimeFormatter = buildTimeFormatter();
    private static DateFormat sDayFormatter = buildDayFormatter();

    public static String formatTimeRange(Date start, Date end) {
        DateFormat timeFormatter = UIUtils.isOnMainThread() ? sTimeFormatter : buildTimeFormatter();
        return String.format("%s - %s", timeFormatter.format(start), timeFormatter.format(end));
    }

    public static String formatDay(Date date) {
        DateFormat dayFormatter = UIUtils.isOnMainThread() ? sDayFormatter : buildDayFormatter();
        String day = dayFormatter.format(date);
        return day.substring(0, 1).toUpperCase() + day.substring(1);
    }

    public static String formatShortTime(Date time) {
        DateFormat timeFormatter = UIUtils.isOnMainThread() ? sTimeFormatter : buildTimeFormatter();
        return timeFormatter.format(time);
    }

    public static DateFormat buildTimeFormatter() {
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return timeFormatter;
    }

    public static DateFormat buildDayFormatter() {
        DateFormat timeFormatter = new SimpleDateFormat("EEEE", Locale.getDefault());
        return timeFormatter;
    }

    public static String formatDayTime(Date date) {
        return String.format("%s %s", formatDay(date), formatShortTime(date));
    }
}
