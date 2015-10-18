package com.doctordark.base;

import com.doctordark.util.Config;
import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import gnu.trove.procedure.TObjectLongProcedure;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Manager to track the playing times of {@link Player}s.
 */
public class PlayTimeManager implements Listener {

    // The total playtime in milliseconds of a users' current session.
    private final TObjectLongMap<UUID> sessionTimestamps = new TObjectLongHashMap<>();

    // The total playtime in milliseconds excluding the time of a users' current session.
    private final TObjectLongMap<UUID> totalPlaytimeMap = new TObjectLongHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0L);

    private final Config config;

    public PlayTimeManager(JavaPlugin plugin) {
        this.config = new Config(plugin, "play-times");
        this.reloadPlaytimeData();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.sessionTimestamps.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        long sessionTime = System.currentTimeMillis() - this.sessionTimestamps.remove(uuid);
        this.totalPlaytimeMap.adjustOrPutValue(uuid, sessionTime, sessionTime);
    }

    /**
     * Loads the play time data from storage.
     */
    public void reloadPlaytimeData() {
        Object object = this.config.get("playing-times");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                this.totalPlaytimeMap.put(UUID.fromString(id), config.getLong("playing-times." + id, 0L));
            }
        }

        long millis = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.sessionTimestamps.put(player.getUniqueId(), millis);
        }
    }

    /**
     * Saves the play time data to storage.
     */
    public void savePlaytimeData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            this.totalPlaytimeMap.adjustValue(uuid, this.sessionTimestamps.get(uuid));
        }

        this.totalPlaytimeMap.forEachEntry(new TObjectLongProcedure<UUID>() {
            @Override
            public boolean execute(UUID uuid, long value) {
                config.set("playing-times." + uuid.toString(), value);
                return true;
            }
        });

        this.config.save();
    }

    /**
     * Gets the session play time of a player.
     *
     * @param uuid the uuid of user to get for
     * @return the session playtime in milliseconds
     */
    public long getSessionPlayTime(UUID uuid) {
        long session = this.sessionTimestamps.get(uuid);
        return session != this.sessionTimestamps.getNoEntryValue() ? System.currentTimeMillis() - session : 0L;
    }

    /**
     * Gets the previous playing time before the current session.
     *
     * @param uuid the uuid of user to get for
     * @return the previous sessions playtime in milliseconds
     */
    public long getPreviousPlayTime(UUID uuid) {
        return this.totalPlaytimeMap.get(uuid);
    }

    /**
     * Gets the total playtime of a user.
     *
     * @param uuid the uuid of user to get for
     * @return the total playtime in milliseconds
     */
    public long getTotalPlayTime(UUID uuid) {
        return this.getSessionPlayTime(uuid) + this.getPreviousPlayTime(uuid);
    }
}
