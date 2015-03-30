package com.doctordark.base.user;

import com.google.common.collect.Maps;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class NameHistory implements ConfigurationSerializable {
    private final String name;
    private final long millis;

    public NameHistory(Map<String, Object> map) {
        if (map.containsKey("name")) {
            this.name = ((String) map.get("name"));
        } else {
            this.name = null;
        }

        if (map.containsKey("millis")) {
            this.millis = Long.parseLong((String) map.get("millis"));
        } else {
            this.millis = System.currentTimeMillis();
        }
    }

    public NameHistory(String name, long millis) {
        this.name = name;
        this.millis = millis;
    }

    public String getName() {
        return this.name;
    }

    public long getMillis() {
        return this.millis;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", getName());
        map.put("millis", Long.toString(getMillis()));
        return map;
    }
}
