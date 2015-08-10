package com.doctordark.base.warp;

import com.doctordark.util.PersistableLocation;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

public class Warp extends PersistableLocation implements ConfigurationSerializable {

    private String name;

    public Warp(String name, Location location) {
        super(location);
        Validate.notNull(name, "Warp name cannot be null");
        Validate.notNull(location, "Warp location cannot be null");

        this.name = name;
    }

    public Warp(Map<String, Object> map) {
        super(map);
        this.name = ((String) map.get("name"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("name", this.name);
        return map;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        Validate.notNull(name, "Warp name cannot be null");
        this.name = name;
    }
}
