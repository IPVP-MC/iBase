package com.doctordark.base.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColouredSignListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        String[] lines = event.getLines();

        if (player == null || !player.hasPermission("base.signs.colour")) {
            return;
        }

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
