package com.doctordark.base.task;

import com.doctordark.base.BasePlugin;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class AutoRestartHandler {

    private static final int[] ALERT_SECONDS = {600, 300, 270, 240, 210, 180, 150, 120, 90, 60, 30, 15, 10, 5, 4, 3, 2, 1};
    private static final long TICKS_DAY = TimeUnit.DAYS.toMillis(1L);

    private final BasePlugin plugin;
    private long current = Long.MIN_VALUE;
    private String reason;
    private BukkitTask task;

    public AutoRestartHandler(BasePlugin plugin) {
        this.plugin = plugin;
        this.scheduleRestart(TICKS_DAY);
    }

    /**
     * Gets the reason for the current restart schedule.
     *
     * @return the reason
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Sets the reason for the current restart schedule.
     *
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Checks if there is a pending restart.
     *
     * @return true if there is a pending restart
     */
    public boolean isPendingRestart() {
        return task != null && current != Long.MIN_VALUE;
    }

    /**
     * Cancels the pending restart if any.
     */
    public void cancelRestart() {
        if (isPendingRestart()) {
            this.task.cancel();
            this.task = null;
            this.current = Long.MIN_VALUE;
        }
    }

    /**
     * Gets the remaining milliseconds until the pending restart applies.
     *
     * @return time in milliseconds
     */
    public long getRemainingMilliseconds() {
        return getRemainingTicks() * 50L;
    }

    /**
     * Gets the remaining ticks until the pending restart applies.
     *
     * @return time in ticks
     */
    public long getRemainingTicks() {
        return current - MinecraftServer.currentTick;
    }

    /**
     * Schedules a restart without a reason in a given amount of milliseconds.
     *
     * @param milliseconds the milliseconds to schedule in
     */
    public void scheduleRestart(long milliseconds) {
        this.scheduleRestart(milliseconds, null);
    }

    /**
     * Schedules a restart with a reason in a given amount of milliseconds.
     *
     * @param millis the milliseconds to schedule in
     * @param reason the reason for restarting
     */
    public void scheduleRestart(final long millis, final String reason) {
        this.cancelRestart();
        this.reason = reason;
        this.current = MinecraftServer.currentTick + (millis / 50L);
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (current == 0L) cancel(); // this restart was cancelled.

                long remainingTicks = getRemainingTicks();
                if (remainingTicks <= 0) {
                    cancel();
                    Bukkit.shutdown(ChatColor.RED + "Server restarting.. Back up momentarily!" + (reason == null ? "" : ChatColor.GOLD + "\n\n" + reason));
                    return;
                }

                long remainingMillis = remainingTicks * 50L;
                if (Ints.contains(AutoRestartHandler.ALERT_SECONDS, (int) (remainingMillis / 1000L))) {
                    Bukkit.broadcastMessage(ChatColor.RED + "Server restarting in " + ChatColor.GOLD +
                            DurationFormatUtils.formatDurationWords(remainingMillis, true, true) + ChatColor.RED + ((reason == null) ||
                            (reason.isEmpty()) ? "." : " [" + ChatColor.GRAY + reason + ChatColor.RED + "]."));
                }
            }
        }.runTaskTimer(plugin, 1L, 20L);
    }
}
