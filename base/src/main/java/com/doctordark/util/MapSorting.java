package com.doctordark.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
/**
 * Source: https://gist.github.com/aadnk/9632795
 */
public class MapSorting {

    // Building block - extract key from entry
    private static final Function EXTRACT_KEY = new Function<Entry<Object, Object>, Object>() {
        @Override
        public Object apply(Entry<Object, Object> input) {
            return input == null ? null : input.getKey();
        }
    };

    // Same as above, only we extract the value
    private static final Function EXTRACT_VALUE = new Function<Entry<Object, Object>, Object>() {
        @Override
        public Object apply(Entry<Object, Object> input) {
            return input == null ? null : input.getValue();
        }
    };

    /**
     * Sort the given map by the value in each entry.
     *
     * @param <T> the key
     * @param <V> the value
     * @param map - map of comparable values.
     * @return A new list with the sort result.
     */
    public static <T, V extends Comparable<V>> List<Entry<T, V>> sortedValues(Map<T, V> map) {
        return sortedValues(map, Ordering.<V>natural());
    }

    /**
     * Sort the given map by the value in each entry.
     *
     * @param <T>             the key
     * @param <V>             the value
     * @param map             - map of comparable values.
     * @param valueComparator - object for comparing each values.
     * @return A new list with the sort result.
     */
    public static <T, V> List<Entry<T, V>> sortedValues(Map<T, V> map, Comparator<V> valueComparator) {
        return Ordering.from(valueComparator).onResultOf(MapSorting.<T, V>extractValue()).sortedCopy(map.entrySet());
    }

    /**
     * Retrieve every key in the entry list in order.
     *
     * @param <T>       the key
     * @param <V>       the value
     * @param entryList - the entry list.
     * @return Every key in order.
     */
    public static <T, V> Iterable<T> keys(List<Entry<T, V>> entryList) {
        return Iterables.transform(entryList, MapSorting.<T, V>extractKey());
    }

    /**
     * Retrieve every value in the entry list in order.
     *
     * @param entryList - the entry list.
     * @param <T>       the key
     * @param <V>       the value
     * @return Every value in order.
     */
    public static <T, V> Iterable<V> values(List<Entry<T, V>> entryList) {
        return Iterables.transform(entryList, MapSorting.<T, V>extractValue());
    }

    @SuppressWarnings("unchecked")
    private static <T, V> Function<Entry<T, V>, T> extractKey() {
        return EXTRACT_KEY;
    }

    @SuppressWarnings("unchecked")
    private static <T, V> Function<Entry<T, V>, V> extractValue() {
        return EXTRACT_VALUE;
    }
}