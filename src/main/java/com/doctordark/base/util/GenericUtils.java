package com.doctordark.base.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GenericUtils {

    public static Field getField(Class<?> clazz, String fieldName) {
        Class<?> tmpClass = clazz;
        while (clazz != null) {
            for (Field field : tmpClass.getDeclaredFields()) {
                String candidateName = field.getName();
                if (candidateName.equals(fieldName)) {
                    field.setAccessible(true);
                    return field;
                }
            }

            tmpClass = tmpClass.getSuperclass();
        }

        return null;
    }

    public static <E> List<E> castList(Object object, Class<E> type) {
        List<E> output = Lists.newArrayList();
        if ((object != null) && ((object instanceof List))) {
            List<?> input = (List) object;
            for (Object value : input) {
                if ((value != null) && (value.getClass() != null)) {
                    if (type.isAssignableFrom(value.getClass())) {
                        E e = type.cast(value);
                        output.add(e);
                    } else {
                        String simpleName = type.getSimpleName();
                        throw new AssertionError("Cannot cast to list! Key " + value + " is not a " + simpleName);
                    }
                }
            }
        }

        return output;
    }

    public static <E> Set<E> castSet(Object object, Class<E> type) {
        Set<E> output = Sets.newHashSet();
        if ((object != null) && ((object instanceof List))) {
            List<?> input = (List) object;
            for (Object value : input) {
                if ((value != null) && (value.getClass() != null)) {
                    if (type.isAssignableFrom(value.getClass())) {
                        E e = type.cast(value);
                        output.add(e);
                    } else {
                        String simpleName = type.getSimpleName();
                        throw new AssertionError("Cannot cast to list! Key " + value + " is not a " + simpleName);
                    }
                }
            }
        }

        return output;
    }

    public static <K, V> Map<K, V> castMap(Object object, Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> output = Maps.newHashMap();
        if ((object != null) && ((object instanceof Map))) {
            Map<?, ?> input = (Map) object;
            String keyClassName = keyClass.getSimpleName();
            String valueClassName = valueClass.getSimpleName();
            for (Object key : input.keySet().toArray()) {
                if ((key == null) || (keyClass.isAssignableFrom(keyClass))) {
                    Object value = input.get(key);
                    if ((value == null) || (valueClass.isAssignableFrom(valueClass))) {
                        K k = keyClass.cast(key);
                        V v = valueClass.cast(value);
                        output.put(k, v);
                    } else {
                        throw new AssertionError("Cannot cast to HashMap: " + keyClassName + ", " + keyClassName + ". Value " + value + " is not a " + keyClassName);
                    }
                } else {
                    throw new AssertionError("Cannot cast to HashMap: " + valueClassName + ", " + valueClassName + ". Key " + key + " is not a " + valueClassName);
                }
            }
        }

        return output;
    }
}
