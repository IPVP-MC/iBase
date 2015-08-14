package com.doctordark.base;

import com.doctordark.util.Config;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.util.gnu.trove.procedure.TObjectLongProcedure;
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

    private final TObjectLongMap<UUID> totalPlaytimeMap = new TObjectLongHashMap<>();  // map used to store total play-times in milliseconds
    private final TObjectLongMap<UUID> sessionTimestamps = new TObjectLongHashMap<>(); // map used to store milliseconds at session joins

    private final Config config;

    public PlayTimeManager(JavaPlugin plugin) {
        this.config = new Config(plugin, "play-times");
        this.reloadPlaytimeData();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        sessionTimestamps.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        totalPlaytimeMap.put(uuid, getTotalPlayTime(uuid));
        sessionTimestamps.remove(uuid);
    }

    /**
     * Loads the play time data from storage.
     */
    public void reloadPlaytimeData() {
        // Load the kit use count.
        Object object = config.get("playing-times");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String id : section.getKeys(false)) {
                totalPlaytimeMap.put(UUID.fromString(id), config.getLong("playing-times." + id, 0L));
            }
        }

        long millis = System.currentTimeMillis();
        for (Player target : Bukkit.getOnlinePlayers()) {
            sessionTimestamps.put(target.getUniqueId(), millis);
        }
    }

    /**
     * Saves the play time data to storage.
     */
    public void savePlaytimeData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            totalPlaytimeMap.put(player.getUniqueId(), getTotalPlayTime(player.getUniqueId()));
        }

        totalPlaytimeMap.forEachEntry(new TObjectLongProcedure<UUID>() {
            @Override
            public boolean execute(UUID uuid, long l) {
                config.set("playing-times." + uuid.toString(), l);
                return false;
            }
        });

        config.save();
    }

    /**
     * Gets the session play time of a player.
     *
     * @param uuid the uuid of player
     * @return the session playing time in millis
     */
    public long getSessionPlayTime(UUID uuid) {
        long session = sessionTimestamps.get(uuid);
        return session != sessionTimestamps.getNoEntryValue() ? System.currentTimeMillis() - session : 0L;
    }

    /**
     * Gets the previous playing time before the current session.
     *
     * @param uuid the uuid of player to get for
     * @return the previous sessions play time in milliseconds
     */
    public long getPreviousPlayTime(UUID uuid) {
        long stamp = totalPlaytimeMap.get(uuid);
        return stamp == totalPlaytimeMap.getNoEntryValue() ? 0L : stamp;
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
