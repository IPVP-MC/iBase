package com.doctordark.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Utility class with useful methods.
 */
public final class Utils {

    private Utils() {
    }

    // Pattern to check if an {@link String} is alphanumeric.
    private static final String ALPHANUMERIC_MATCHER = "^[a-zA-Z0-9]*$";

    /**
     * Checks if an {@link String} is alphanumeric.
     *
     * @param input the input to check
     * @return true if the input is alphanumeric
     */
    public static boolean isAlphanumeric(String input) {
        return input.matches(ALPHANUMERIC_MATCHER);
    }

    /**
     * Checks if a {@link Collection} contains a {@link String}.
     * <p>This is not case-sensitive.</p>
     * <p>This is generic friendly.</p>
     *
     * @param collection the {@link Collection} to check
     * @param input      the {@link String} to search for
     * @return true if {@link Collection} contains input
     */
    public static boolean containsIgnoreCase(Collection collection, String input) {
        for (Object object : collection) {
            if (object instanceof String) {
                String next = (String) object;
                if (StringUtils.containsIgnoreCase(next, input)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Parses a {@link String} into a {@link Long} using
     * characters such as 'm' or 'h' to represent minutes or hours.
     *
     * @param input the input to parse
     * @return the parsed time in milliseconds
     */
    public static long parse(String input) {
        long result = 0;
        String number = "";
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (Character.isDigit(character)) {
                number += character;
            } else if (Character.isLetter(character) && !number.isEmpty()) {
                result += convert(Integer.parseInt(number), character);
                number = "";
            }
        }

        return result;
    }

    /**
     * Converts a number to a duration in milliseconds
     * based on its character.
     *
     * @param number the number to convert
     * @param unit   the unit it is being converted to
     * @return the converted value in milliseconds
     */
    private static long convert(int number, char unit) {
        switch (unit) {
            case 'y' | 'Y':
                return TimeUnit.DAYS.toMillis(number * 365);
            case 'd' | 'D':
                return number * 1000 * 60 * 60 * 24;
            case 'h' | 'H':
                return number * 1000 * 60 * 60;
            case 'm' | 'M':
                return number * 1000 * 60;
            case 's' | 'S':
                return number * 1000;
            default:
                return 0;
        }
    }

    /**
     * Parses a {@link Float} from a {@link String}.
     *
     * @param id the {@link String} to parse
     * @return the {@link Float}, or null if is not
     */
    public static Float getFloat(String id) {
        try {
            return Float.parseFloat(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Parses a {@link Long} from a {@link String}.
     *
     * @param id the {@link String} to parse
     * @return the {@link Long}, or null if is not
     */
    public static Long getLong(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Parses an {@link Integer} from a {@link String}.
     *
     * @param id the {@link String} to parse
     * @return the {@link Integer}, or null if is not
     */
    public static Integer getInteger(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Parses a {@link Double} from a {@link String}.
     *
     * @param id the {@link String} to parse
     * @return the {@link Double}, or null if is not
     */
    public static Double getDouble(String id) {
        try {
            return Double.parseDouble(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Formats a {@link Number} into a formatted {@link String} without trailing zeroes.
     *
     * @param number the {@link Number} to format
     * @return the formatted {@link String}
     */
    public static String format(Number number) {
        return format(number, 5);
    }

    /**
     * Formats a {@link Number} into a formatted {@link String} without
     * trailing zeroes with a specific amount of decimal places.
     *
     * @param number        the {@link Number} to format
     * @param decimalPlaces the amount of decimal places to use
     * @return the formatted {@link String}
     */
    public static String format(Number number, int decimalPlaces) {
        return format(number, decimalPlaces, RoundingMode.HALF_DOWN);
    }

    /**
     * Formats a {@link Number} into a formatted {@link String} without
     * trailing zeroes with a specific amount of decimal places using a
     * specific {@link RoundingMode}.
     *
     * @param number        the {@link Number} to format
     * @param decimalPlaces the amount of decimal places to use
     * @param roundingMode  the {@link RoundingMode} to use
     * @return the formatted {@link String}
     */
    public static String format(Number number, int decimalPlaces, RoundingMode roundingMode) {
        Validate.notNull(number, "The number cannot be null");

        BigDecimal bigDecimal = new BigDecimal(number.toString());
        // Don't show 0.0
        if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        return bigDecimal.setScale(decimalPlaces, roundingMode).stripTrailingZeros().toPlainString();
    }
}
