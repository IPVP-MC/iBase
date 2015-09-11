package com.doctordark.base.warp;

import com.doctordark.util.PersistableLocation;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class Warp extends PersistableLocation implements ConfigurationSerializable {

    private String name;

    public Warp(String name, Location location) {
        super(location);
        Preconditions.checkNotNull(name, "Warp name cannot be null");
        Preconditions.checkNotNull(location, "Warp location cannot be null");

        this.name = name;
    }

    public Warp(Map<String, Object> map) {
        super(map);
        this.name = (String) map.get("name");
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
        Preconditions.checkNotNull(name, "Warp name cannot be null");
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Warp warp = (Warp) o;

        return !(name != null ? !name.equals(warp.name) : warp.name != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
