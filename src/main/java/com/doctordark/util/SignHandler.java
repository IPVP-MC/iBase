package com.doctordark.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SignHandler implements Listener {

    private final Multimap<UUID, SignChange> signUpdateMap = HashMultimap.create();
    private final JavaPlugin plugin;

    public SignHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerQuitEvent event) {
        cancelTasks(event.getPlayer(), null, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        cancelTasks(event.getPlayer(), null, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        cancelTasks(event.getPlayer(), null, false);
    }

    /**
     * Shows line changes on a {@link Sign} for a {@link Player}.
     *
     * @param player      the {@link Player} to update for
     * @param sign        the {@link Sign} to update for
     * @param newLines    the new {@link Sign} lines
     * @param ticks       the ticks to show change for
     * @param forceChange if it should ignore tick delay
     * @return true if the lines were updated
     */
    public boolean showLines(Player player, Sign sign, String[] newLines, long ticks, boolean forceChange) {
        String[] lines = sign.getLines();

        // Don't update the sign if it doesn't need to be, cancel any others.
        if (Arrays.equals(lines, newLines)) {
            return false;
        }

        Collection<SignChange> signChanges = getSignChanges(player);
        for (Iterator<SignChange> iterator = signChanges.iterator(); iterator.hasNext(); ) {
            SignChange signChange = iterator.next();
            if (signChange.sign.equals(sign)) {
                if (!forceChange && Arrays.equals(signChange.newLines, newLines)) {
                    return false;
                }

                signChange.runnable.cancel();
                iterator.remove();
                break;
            }
        }

        // Update it here, with a task to revert it.
        Location location = sign.getLocation();
        player.sendSignChange(location, newLines);

        SignChange signChange;
        if (signChanges.add(signChange = new SignChange(sign, newLines))) {
            Block block = sign.getBlock();
            BlockState previous = block.getState();

            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (signUpdateMap.remove(player.getUniqueId(), signChange) && previous.equals(block.getState())) {
                        player.sendSignChange(location, lines);
                    }
                }
            };

            runnable.runTaskLater(plugin, ticks);
            signChange.runnable = runnable;
        }

        return true;
    }

    /**
     * Gets the {@link com.doctordark.hcfnew.util.SignHandler.SignChange}s shown to a {@link Player}.
     *
     * @param player the {@link Player} to get for
     * @return collection of {@link com.doctordark.hcfnew.util.SignHandler.SignChange}s
     */
    public Collection<SignChange> getSignChanges(Player player) {
        return signUpdateMap.get(player.getUniqueId());
    }

    /**
     * Cancels {@link com.doctordark.hcfnew.util.SignHandler.SignChange}s for a {@link Sign}.
     *
     * @param sign the {@link Sign} to cancel for, or null to cancel all
     */
    public void cancelTasks(@Nullable Sign sign) {
        Iterator<SignChange> iterator = signUpdateMap.values().iterator();
        while (iterator.hasNext()) {
            SignChange signChange = iterator.next();
            if (sign == null || signChange.sign.equals(sign)) {
                signChange.runnable.cancel();
                signChange.sign.update();
                iterator.remove();
            }
        }
    }

    /**
     * Cancels {@link com.doctordark.hcfnew.util.SignHandler.SignChange}s for a {@link Player}.
     *
     * @param player      the {@link Player} to cancel for
     * @param sign        the {@link Sign} to cancel for, or null to cancel all
     * @param revertLines if the {@link Player} should receive a line revert packet
     */
    public void cancelTasks(Player player, @Nullable Sign sign, boolean revertLines) {
        UUID uuid = player.getUniqueId();
        Iterator<Map.Entry<UUID, SignChange>> iterator = signUpdateMap.entries().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, SignChange> entry = iterator.next();
            if (entry.getKey().equals(uuid)) {
                SignChange signChange = entry.getValue();
                if (sign == null || signChange.sign.equals(sign)) {
                    if (revertLines) {
                        player.sendSignChange(signChange.sign.getLocation(), signChange.sign.getLines());
                    }

                    signChange.runnable.cancel();
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Represents a change in faking the lines of a {@link Sign} for a {@link Player}.
     */
    private static class SignChange {

        public BukkitRunnable runnable;
        public final Sign sign;
        public final String[] newLines;

        public SignChange(Sign sign, String[] newLines) {
            this.sign = sign;
            this.newLines = newLines;
        }
    }
}
