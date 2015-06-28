package com.doctordark.util.bossbar;

import com.doctordark.base.BasePlugin;
import com.doctordark.util.bossbar.BossBarEntry;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

/**
 * Management of {@link BossBar}s for {@link Player}s.
 */
public class BossBarManager {

    private static final Map<UUID, BossBarEntry> bossBars = Maps.newHashMap();

    private static JavaPlugin plugin;

    /**
     * Hooks this {@link BossBarManager} to a {@link JavaPlugin}.
     */
    public static void hook() {
        Preconditions.checkArgument(BossBarManager.plugin == null, "BossBarManager is already hooked");
        BossBarManager.plugin = BasePlugin.getPlugin();
    }

    /**
     * Unhooks this {@link BossBarManager}.
     */
    public static void unhook() {
        Preconditions.checkArgument(BossBarManager.plugin != null, "BossBarManager is already unhooked");

        for (UUID uuid : bossBars.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                // Hide all of the active Boss Bars.
                hideBossBar(player);
            }
        }

        BossBarManager.plugin = null;
    }

    public static Map<UUID, BossBarEntry> getBossBars() {
        return bossBars;
    }

    /**
     * Checks if a {@link BossBar} is currently being shown to a {@link Player}.
     *
     * @return true if the {@link Player} has a shown {@link BossBar}
     */
    public static boolean isShowingBossBar(Player player) {
        return BossBarManager.getShownBossBar(player) != null;
    }

    /**
     * Gets the {@link BossBar} that is currently being shown to a {@link Player}.
     *
     * @param player the {@link Player} to get for
     * @return the shown {@link BossBar}, or null
     */
    public static BossBar getShownBossBar(Player player) {
        UUID uuid = player.getUniqueId();
        BossBarEntry bossBarEntry = BossBarManager.bossBars.get(uuid);
        return bossBarEntry != null ? bossBarEntry.getBossBar() : null;
    }

    /**
     * Hides the current {@link BossBar} shown to a player.
     *
     * @param player the {@link Player} to hide for
     * @return the {@link BossBar} that was hidden, or null if wasn't showing one
     */
    public static BossBar hideBossBar(Player player) {
        BossBarEntry entry = bossBars.get(player.getUniqueId());
        if (entry == null) return null;

        BossBar bossBar = entry.getBossBar();
        BukkitTask bukkitTask = entry.getCancelTask();

        if (bukkitTask != null) bukkitTask.cancel(); // cancel the destroy task.
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bossBar.destroyPacket);
        bossBars.remove(player.getUniqueId());

        return bossBar;
    }

    /**
     * Displays a {@link BossBar} that doesn't expire for a {@link Player}.
     *
     * @see #showBossBar(BossBar, Player, long)
     */
    public static void showBossBar(BossBar bossBar, Player player) {
        BossBarManager.showBossBar(bossBar, player, 0L);
    }

    /**
     * Displays a {@link BossBar} to a {@link Player} for a given amount of ticks.
     *
     * @param bossBar the {@link BossBar} to display
     * @param player  the {@link Player} to display to
     * @param ticks   the time in ticks to display for
     */
    public static void showBossBar(BossBar bossBar, Player player, long ticks) {
        BossBar current = getShownBossBar(player);
        if (current != null) {
            if (current.equals(bossBar)) return; // Don't try and show the same bar.
            hideBossBar(player);                 // Don't show two Boss Bars to the same player at once.
        }

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(bossBar.spawnPacket);

        BukkitTask bukkitTask;
        if (ticks <= 0L) {
            bukkitTask = null;
        } else {
            Validate.isTrue(BossBarManager.plugin != null, "Cannot start destroy runnable as plugin wasn't hooked correctly.");
            bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                    hideBossBar(player);
                }
            }.runTaskLater(plugin, ticks);
        }

        bossBars.put(player.getUniqueId(), new BossBarEntry(bossBar, bukkitTask));
        connection.sendPacket(bossBar.spawnPacket);
    }
}