package com.doctordark.base.user;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the history a {@link BaseUser}s name due to
 * Mojangs name changing feature.
 */
public class NameHistory implements ConfigurationSerializable {

    @Getter
    private final String name;

    @Getter
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
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("millis", Long.toString(millis));
        return map;
    }
}
