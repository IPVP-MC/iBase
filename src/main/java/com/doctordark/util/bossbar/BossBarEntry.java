package com.doctordark.util.bossbar;

import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;

/**
 * Represents a entry of a {@link BossBar}.
 */
public class BossBarEntry {

    private final BossBar bossBar;
    private final BukkitTask cancelTask;

    /**
     * Constructs a new {@link BossBar} with a given task to cancel.
     *
     * @param bossBar    the {@link BossBar}
     * @param cancelTask the {@link BukkitTask}
     */
    public BossBarEntry(BossBar bossBar, @Nullable BukkitTask cancelTask) {
        this.bossBar = bossBar;
        this.cancelTask = cancelTask;
    }

    /**
     * Gets the {@link BossBar} of this entry.
     *
     * @return the {@link BossBar}
     */
    public BossBar getBossBar() {
        return bossBar;
    }

    /**
     * Gets the cancel {@link BukkitTask} of this entry.
     *
     * @return the cancel {@link BukkitTask}
     */
    public BukkitTask getCancelTask() {
        return cancelTask;
    }
}
