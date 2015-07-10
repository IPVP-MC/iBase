package com.doctordark.util;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * This is used for generic Java utilities.
 */
public final class JavaUtils {

    private static final Pattern UUID_PATTERN;

    static {
        UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");
    }

    private JavaUtils() {
    }

    /**
     * Checks if an {@link String} is a {@link java.util.UUID}.
     *
     * @param input the input to check
     * @return true if the input is a {@link java.util.UUID}
     */
    public static boolean isUUID(String input) {
        return UUID_PATTERN.matcher(input).find();
    }

    /**
     * Returns the given string if it is alphanumeric.
     *
     * @param string the string to test
     * @return true if it is alphanumeric
     */
    public static boolean isAlphanumeric(String string) {
        return CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.WHITESPACE).matchesAllOf(string);
    }

    /**
     * Returns the given collection if it has an object that contains a
     * non case-sensitive string.
     *
     * @param collection the collection to test
     * @param string     the string to test
     * @return true if the collection contains string
     */
    public static boolean containsIgnoreCase(Collection<?> collection, String string) {
        boolean result = false;
        for (Object object : collection) {
            final String next;
            if (object instanceof String) {
                next = (String) object;
            } else {
                next = String.valueOf(object);
            }

            if (StringUtils.containsIgnoreCase(next, string)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * Formats a number to 5 decimal places using {@link RoundingMode#HALF_DOWN}.
     *
     * @param number the number to format
     * @return the formatted string
     */
    public static String format(Number number) {
        return format(number, 5);
    }

    /**
     * Formats a number with a given amount of decimal places using
     * {@link RoundingMode#HALF_DOWN}.
     *
     * @param number        the number to format
     * @param decimalPlaces the decimal places
     * @return the formatted string
     */
    public static String format(Number number, int decimalPlaces) {
        return format(number, decimalPlaces, RoundingMode.HALF_DOWN);
    }

    /**
     * Formats a number with a given amount of decimal places and a RoundingMode.
     *
     * @param number        the number to format
     * @param decimalPlaces the decimal places
     * @param roundingMode  the rounding mode
     * @return the formatted string
     */
    public static String format(Number number, int decimalPlaces, RoundingMode roundingMode) {
        Validate.notNull(number, "The number cannot be null");
        double doubleValue = number.doubleValue();
        if (doubleValue == 0.0D) {
            return "0";
        } else {
            BigDecimal bigDecimal = new BigDecimal(Double.toString(doubleValue));
            return bigDecimal.setScale(decimalPlaces, roundingMode).stripTrailingZeros().toPlainString();
        }
    }

    /**
     * Parses a string describing measures of time (e.g. �1d 1m 1s�) to milliseconds
     *
     * @param string the string to parse
     * @return the parsed time in milliseconds
     */
    public static Long parse(String input) {
        Long result = null;
        String number = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number += c;
            } else if (Character.isLetter(c) && !number.isEmpty()) {
                if (result == null) result = 0L;
                result += convert(Integer.parseInt(number), c);
                number = "";
            }
        }

        return result;
    }

    private static Long convert(int value, char unit) {
        switch (unit) {
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'd' | 'D':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h' | 'H':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's' | 'S':
                return value * TimeUnit.SECONDS.toMillis(1L);
            default:
                return null;
        }
    }
}