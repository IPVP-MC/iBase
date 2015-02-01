package com.doctordark.base.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static void main(String[] args) {
        long millis = (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(25));
        System.out.println(String.format("%d hours, %d min, %d sec", getHours(millis), getMinutes(millis), getSeconds(millis)));
    }

    private static long getTime(long milliseconds) {
        return TimeUnit.MILLISECONDS.toSeconds(milliseconds);
    }

    /**
     * Gets the hours of a day from milliseconds.
     *
     * @param milliseconds the millis to get from
     * @return the hours of day
     */
    public static long getHours(long milliseconds) {
        long time = getTime(milliseconds);
        long day = TimeUnit.SECONDS.toDays(milliseconds) / 1000L;
        return TimeUnit.SECONDS.toHours(time) - (day * 24);
    }

    /**
     * Gets the minutes of a day from milliseconds.
     *
     * @param milliseconds the millis to get from
     * @return the minutes of day
     */
    public static long getMinutes(long milliseconds) {
        long time = getTime(milliseconds);
        return TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
    }

    /**
     * Gets the seconds of a day from milliseconds.
     *
     * @param milliseconds the seconds to get from
     * @return the seconds of day
     */
    public static long getSeconds(long milliseconds) {
        long time = getTime(milliseconds);
        return TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);
    }
}
