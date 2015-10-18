package com.doctordark.util.bossbar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

@AllArgsConstructor
public class BossBarEntry {

    @Getter
    private final BossBar bossBar;

    @Getter
    private BukkitTask removeTask;

    public BossBarEntry(BossBar bossBar) {
        this.bossBar = bossBar;
    }
}
