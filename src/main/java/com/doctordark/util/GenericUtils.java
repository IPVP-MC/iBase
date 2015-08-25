package com.doctordark.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for managing Generics in Java.
 *
 * Some of the source has been taken from: http://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
 */
public final class GenericUtils {

    private GenericUtils() {
    }

    /**
     * Converts an object to a list safely.
     *
     * @param object the object to convert
     * @param type   the class type to convert to
     * @param <E>    the generic type
     * @return the safely converted list
     */
    public static <E> List<E> createList(Object object, Class<E> type) {
        List<E> output = new ArrayList<>();

        if (object != null && object instanceof List<?>) {
            List<?> input = (List<?>) object;

            for (Object value : input) {
                if (value == null || value.getClass() == null) {
                    continue;
                }

                if (type.isAssignableFrom(value.getClass())) {
                    E e = type.cast(value);
                    output.add(e);
                } else {
                    String simpleName = type.getSimpleName();
                    throw new AssertionError("Cannot cast to list! Key " + value + " is not a " + simpleName);
                }
            }
        }

        return output;
    }

    /**
     * Converts an object to a set safely.
     *
     * @param object the object to convert
     * @param type   the class type to convert to
     * @param <E>    the generic type
     * @return the safely converted set
     */
    public static <E> Set<E> castSet(Object object, Class<E> type) {
        Set<E> output = new HashSet<>();

        if (object != null && object instanceof List<?>) {
            List<?> input = (List<?>) object;

            for (Object value : input) {
                if (value == null || value.getClass() == null) {
                    continue;
                }

                if (type.isAssignableFrom(value.getClass())) {
                    E e = type.cast(value);
                    output.add(e);
                } else {
                    String simpleName = type.getSimpleName();
                    throw new AssertionError("Cannot cast to list! Key " + value + " is not a " + simpleName);
                }
            }
        }

        return output;
    }

    /**
     * Casts an object to a HashMap safely.
     *
     * @param object     the object to cast
     * @param keyClass   the key type of map
     * @param valueClass the value type of map
     * @param <K>        the key object
     * @param <V>        the value object
     * @return the safely casted map
     */
    public static <K, V> Map<K, V> castMap(Object object, Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> output = new HashMap<>();

        if (object != null && object instanceof Map<?, ?>) {
            Map<?, ?> input = (Map<?, ?>) object;
            String keyClassName = keyClass.getSimpleName();
            String valueClassName = valueClass.getSimpleName();
            for (Object key : input.keySet().toArray()) {
                if (key != null && !keyClass.isAssignableFrom(key.getClass())) {
                    throw new AssertionError("Cannot cast to HashMap: " + keyClassName + ", " + keyClassName + ". Value " + valueClassName + " is not a " + keyClassName);
                }

                Object value = input.get(key);
                if (value != null && !valueClass.isAssignableFrom(value.getClass())) {
                    throw new AssertionError("Cannot cast to HashMap: " + valueClassName + ", " + valueClassName + ". Key " + key + " is not a " + valueClassName);
                }

                output.put(keyClass.cast(key), valueClass.cast(value));
            }
        }

        return output;
    }
}
