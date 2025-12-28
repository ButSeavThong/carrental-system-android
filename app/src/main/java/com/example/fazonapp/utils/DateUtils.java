package com.example.fazonapp.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    // Format timestamp to readable date
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Calculate days between two dates
    public static long getDaysBetween(long startDate, long endDate) {
        long diff = endDate - startDate;
        return TimeUnit.MILLISECONDS.toDays(diff);
    }

    // Get current timestamp
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}