package com.youssef.barber.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    private static final String TIME_FORMAT = "hh:mm a";
    private static final String DATE_FORMAT = "EEE, MMM d, yyyy";
    private static final String DATE_TIME_FORMAT = "MMM d, yyyy hh:mm a";

    // Format time (e.g., "02:30 PM")
    public static String formatTime(long timeInMillis) {
        return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
                .format(new Date(timeInMillis));
    }

    // Format date (e.g., "Mon, Jun 5, 2023")
    public static String formatDate(long timeInMillis) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                .format(new Date(timeInMillis));
    }

    // Format date and time (e.g., "Jun 5, 2023 02:30 PM")
    public static String formatDateTime(long timeInMillis) {
        return new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(timeInMillis));
    }

    // Generate time slots between start and end time (30-minute intervals by default)
    public static List<String> generateTimeSlots(int startHour, int endHour, int intervalMinutes) {
        List<String> timeSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);

        while (calendar.before(endCalendar)) {
            timeSlots.add(formatTime(calendar.getTimeInMillis()));
            calendar.add(Calendar.MINUTE, intervalMinutes);
        }

        return timeSlots;
    }

    // Check if a time slot is available (not in the past)
    public static boolean isTimeSlotAvailable(long slotTime) {
        return slotTime > System.currentTimeMillis();
    }

    // Calculate duration between two times in minutes
    public static long getDurationMinutes(long startTime, long endTime) {
        return TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
    }

    // Convert time string back to milliseconds
    public static long parseTimeString(String timeString) {
        try {
            return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
                    .parse(timeString)
                    .getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    // Get current time in formatted string
    public static String getCurrentTimeFormatted() {
        return formatTime(System.currentTimeMillis());
    }

    // Get current date in formatted string
    public static String getCurrentDateFormatted() {
        return formatDate(System.currentTimeMillis());
    }

    // Check if two time periods overlap
    public static boolean isOverlapping(long start1, long end1, long start2, long end2) {
        return start1 < end2 && start2 < end1;
    }
}