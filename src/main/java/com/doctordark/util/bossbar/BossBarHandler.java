package com.doctordark.util.bossbar;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarHandler implements Listener {

    private final Map<UUID, BossBarEntry> bossBars = new HashMap<>();
    private final JavaPlugin plugin;

    public BossBarHandler(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, this.plugin = plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BossBarEntry attached = this.bossBars.get(player.getUniqueId());
        if (attached != null && attached.getBossBar().isReshowOnLogin()) {
            attached.getBossBar().show(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BossBarEntry attached = this.bossBars.get(player.getUniqueId());
        if (attached != null) {
            if (attached.getRemoveTask() != null) {
                attached.getRemoveTask().cancel();
            }

            if (attached.getBossBar().isReshowOnLogin()) {
                attached.getBossBar().hide(player);
                this.bossBars.remove(player.getUniqueId());
            } else {
                this.setBossBar(player, null);
            }
        }
    }

    public BossBar getBossBar(Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null");

        BossBarEntry entry = bossBars.get(player.getUniqueId());
        return entry != null ? entry.getBossBar() : null;
    }

    public void setBossBar(Player player, @Nullable BossBar bossBar) {
        this.setBossBar(player, bossBar, -1);
    }

    public void setBossBar(Player player, @Nullable BossBar bossBar, int ticks) {
        Preconditions.checkNotNull(player, "Player cannot be null");

        final BossBarEntry previous;
        if (bossBar == null) {
            previous = this.bossBars.remove(player.getUniqueId());
        } else {
            Preconditions.checkArgument(ticks > 0, "Ticks must be positive", ticks);

            bossBar.addViewer(player);
            previous = this.bossBars.put(player.getUniqueId(), new BossBarEntry(bossBar, new BukkitRunnable() {
                @Override
                public void run() {
                    setBossBar(player, null);
                }
            }.runTaskLater(this.plugin, ticks)));
        }

        if (previous != null && previous.getBossBar() != bossBar) {
            previous.getBossBar().removeViewer(player);
        }
    }
}
