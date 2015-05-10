package com.doctordark.base.user;

import com.google.common.collect.Maps;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

/**
 * Represents the history a {@link BaseUser}s name due to
 * Mojangs name changing feature.
 */
public class NameHistory implements ConfigurationSerializable {

    private final String name;
    private final long millis;

    /**
     * Constructs a {@link NameHistory} from a map.
     *
     * @param map the map to construct from
     */
    public NameHistory(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.millis = Long.parseLong((String) map.get("millis"));
    }

    /**
     * Constructs a {@link NameHistory} with a given name and time of creation.
     *
     * @param name   the name
     * @param millis the milliseconds
     */
    public NameHistory(String name, long millis) {
        this.name = name;
        this.millis = millis;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", getName());
        map.put("millis", Long.toString(getMillis()));
        return map;
    }

    /**
     * Gets the name of this {@link NameHistory}.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the milliseconds this {@link NameHistory} was found at.
     *
     * @return the milliseconds
     */
    public long getMillis() {
        return millis;
    }
}
