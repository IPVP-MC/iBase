package com.doctordark.base.task;

import com.doctordark.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class AnnouncementHandler extends BukkitRunnable {

    private final BasePlugin plugin;

    public AnnouncementHandler(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<String> announcements = this.plugin.getServerHandler().getAnnouncements();
        if (!announcements.isEmpty()) {
            String next = announcements.get(0);
            Bukkit.broadcastMessage(next);
            Collections.rotate(announcements, -1);
        }
    }
}
