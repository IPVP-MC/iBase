package com.doctordark.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeReader {

    private final Map<String, Long> units = Maps.newConcurrentMap();

    private static final String UNIT_PATTERN = "\\w+";
    private static final Pattern ITEM_PATTERN = Pattern.compile("(\\d+)\\s*(" + UNIT_PATTERN + ")");

    /**
     * Add a new time unit for this {@link TimeReader}.
     *
     * @param value the unit's modifier value (multiplier from milliseconds, e.g. 1000)
     * @param units the units, e.g. "s"
     * @return self reference for chaining
     */
    public TimeReader addUnit(final long value, final String... units) {
        for (String unit : units) {
            Preconditions.checkArgument(value >= 0 && unit.matches(UNIT_PATTERN));
            this.units.put(unit.toLowerCase(), value);
        }

        return this;
    }

    /**
     * Parse a string using the defined units.
     *
     * @return the resulting number of milliseconds
     */
    public long parse(final String input) {
        long value = 0L;
        final Matcher matcher = ITEM_PATTERN.matcher(input);
        while (matcher.find()) {
            final long modifier = Long.parseLong(matcher.group(1));
            final String unit = matcher.group(2).toLowerCase();
            if (units.containsKey(unit)) {
                value += units.get(unit) * modifier;
            }
        }

        return value;
    }
}