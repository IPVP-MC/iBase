package com.doctordark.base.listener.module;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColouredSignListener implements Listener {

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        String[] lines = event.getLines();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
