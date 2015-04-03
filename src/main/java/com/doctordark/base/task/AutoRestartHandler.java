package com.doctordark.base.task;

import com.doctordark.base.BasePlugin;
import net.minecraft.util.com.google.common.primitives.Ints;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class AutoRestartHandler {

    private static final int[] ALERT_SECONDS = {
            600, 300, 270, 240,
            210, 180, 150, 120,
            90, 60, 30, 15, 10,
            5, 4, 3, 2, 1
    };

    private String reason;
    private int remainingSeconds;
    private BukkitRunnable runnable;
    private final BasePlugin plugin;

    public AutoRestartHandler(BasePlugin plugin) {
        this.plugin = plugin;
        scheduleRestart((int) TimeUnit.HOURS.toSeconds(20L));
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isPendingRestart() {
        return this.runnable != null;
    }

    public int getRemainingSeconds() {
        return this.remainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public long getRemainingMillis() {
        return this.remainingSeconds * 1000L;
    }

    public void cancelRestart() {
        if (isPendingRestart()) {
            this.runnable.cancel();
            this.runnable = null;
            this.remainingSeconds = 0;
        }
    }

    public void scheduleRestart(int seconds) {
        scheduleRestart(seconds, null);
    }

    public void scheduleRestart(int seconds, final String reason) {
        final Server server = Bukkit.getServer();
        cancelRestart();

        this.reason = reason;
        this.remainingSeconds = seconds;
        this.runnable = new BukkitRunnable() {
            public void run() {
                if (remainingSeconds == 0) {
                    if (reason != null && !reason.isEmpty()) {
                        for (Player player : server.getOnlinePlayers()) {
                            player.kickPlayer(ChatColor.RED + "Server restarting.. Back up momentarily!" + ChatColor.GOLD + "\n\n" + reason);
                        }
                    }

                    server.shutdown();
                } else if (Ints.contains(AutoRestartHandler.ALERT_SECONDS, remainingSeconds)) {
                    server.broadcastMessage(ChatColor.RED + "Server restarting in " + ChatColor.GOLD +
                            DurationFormatUtils.formatDurationWords(remainingSeconds * 1000L, true, true) + ChatColor.RED + ((reason == null) ||
                            (reason.isEmpty()) ? "." : " [" + ChatColor.GRAY + reason + ChatColor.RED + "]."));
                }

                remainingSeconds--;
            }
        };

        runnable.runTaskTimer(plugin, 20L, 20L);
    }
}
