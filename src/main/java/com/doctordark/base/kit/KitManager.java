package com.doctordark.base.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Manager used to handle all of the {@link Kit}s.
 */
public interface KitManager {

    long MIN_PLAYTIME_KITS = TimeUnit.HOURS.toMillis(2L);
    int UNLIMITED_USES = Integer.MAX_VALUE;

    /**
     * Gets the of {@link Kit}s held by this manager.
     *
     * @return collection of {@link Kit}s
     */
    List<Kit> getKits();

    /**
     * Checks if this manager is holding a {@link Kit}.
     *
     * @param kit the {@link Kit} to check
     * @return true if manager is holding the {@link Kit}
     */
    boolean containsKit(Kit kit);

    /**
     * Saves a {@link Kit} to this manager.
     *
     * @param kit the {@link Kit} to save
     */
    void createKit(Kit kit);

    /**
     * Removes a {@link Kit} from this manager.
     *
     * @param kit the {@link Kit} to remove
     */
    void removeKit(Kit kit);

    /**
     * Gets a {@link Kit} with a name of string.
     *
     * @param id the {@link Kit} to lookup
     * @return the {@link Kit} with name of id
     */
    Kit getKit(String id);

    /**
     * Gets the Kit {@link Inventory} GUI for a {@link Player}.
     *
     * @param player the {@link Player} to get for
     * @return the {@link Inventory} of {@link Kit}s for {@link Player}
     */
    Inventory getGui(Player player);

    /**
     * Loads the {@link Kit} data from storage.
     */
    void reloadKitData();

    /**
     * Saves the {@link Kit} data to storage.
     */
    void saveKitData();
}