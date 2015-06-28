 package com.doctordark.base;

import com.doctordark.util.Config;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

/**
 * Manager to track the playing times of {@link Player}s.
 */
public class PlayTimeManager implements Listener {

    private final Map<UUID, Long> totalPlaytimeMap = Maps.newHashMap();  // map used to store total play-times in milliseconds
    private final Map<UUID, Long> sessionTimestamps = Maps.newHashMap(); // map used to store milliseconds at session joins

    private final Config config;

    public PlayTimeManager(JavaPlugin plugin) {
        this.config = new Config(plugin, "play-times");
        this.reloadPlaytimeData();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        sessionTimestamps.put(uuid, System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        totalPlaytimeMap.put(uuid, getTotalPlayTime(uuid));
        sessionTimestamps.remove(uuid);
    }

    /**
     * Loads the play time data from storage.
     */
    public void reloadPlaytimeData() {
        // Load the kit use count.
        Object object = config.get("playing-times");
        if (object != null && object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                totalPlaytimeMap.put(UUID.fromString(id), config.getLong("playing-times." + id, 0L));
            }
        }

        long millis = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            sessionTimestamps.put(player.getUniqueId(), millis);
        }
    }

    /**
     * Saves the play time data to storage.
     */
    public void savePlaytimeData() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            totalPlaytimeMap.put(player.getUniqueId(), getTotalPlayTime(player.getUniqueId()));
        }

        Map<String, Long> saveMap = Maps.newHashMap();
        for (Map.Entry<UUID, Long> entry : totalPlaytimeMap.entrySet()) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }

        config.set("playing-times", saveMap);
        config.save();
    }

    /**
     * Gets the session play time of a player.
     *
     * @param uuid the uuid of player
     * @return the session playing time in millis
     */
    public long getSessionPlayTime(UUID uuid) {
        if (sessionTimestamps.containsKey(uuid)) {
            return System.currentTimeMillis() - sessionTimestamps.get(uuid);
        } else {
            return 0L;
        }
    }

    /**
     * Gets the previous playing time before the current session.
     *
     * @param uuid the uuid of player to get for
     * @return the previous sessions play time in milliseconds
     */
    public long getPreviousPlayTime(UUID uuid) {
        if (totalPlaytimeMap.containsKey(uuid)) {
            return totalPlaytimeMap.get(uuid);
        } else {
            return 0L;
        }
    }

    /**
     * Gets the total play time of a player.
     *
     * @param uuid the uuid of player
     * @return the playing time in millis
     */
    public long getTotalPlayTime(UUID uuid) {
        return getSessionPlayTime(uuid) + getPreviousPlayTime(uuid);
    }
}
