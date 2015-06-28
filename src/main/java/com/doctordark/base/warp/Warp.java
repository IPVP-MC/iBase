package com.doctordark.base.warp;

import com.doctordark.util.PersistableLocation;
import com.google.common.collect.Maps;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class Warp implements ConfigurationSerializable {

    private String name;
    private Location location;

    public Warp(String name, Location location) {
        Validate.notNull(name, "Warp name cannot be null");
        Validate.notNull(location, "Warp location cannot be null");

        this.name = name;
        this.location = location;
    }

    public Warp(Map<String, Object> map) {
        if (map.containsKey("name")) {
            this.name = ((String) map.get("name"));
        }

        if (map.containsKey("location")) {
            this.location = ((PersistableLocation) map.get("location")).getLocation();
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", getName());
        map.put("location", new PersistableLocation(location));
        return map;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        Validate.notNull(name, "Warp name cannot be null");
        this.name = name;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        Validate.notNull(location, "Warp location cannot be null");
        this.location = location;
    }
}
