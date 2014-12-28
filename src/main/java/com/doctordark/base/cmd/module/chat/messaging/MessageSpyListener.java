package com.doctordark.base.cmd.module.chat.messaging;

import com.doctordark.base.BasePlugin;
import com.doctordark.base.cmd.module.chat.messaging.event.PlayerMessageEvent;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MessageSpyListener implements Listener {

    private final BasePlugin plugin;

    public MessageSpyListener(BasePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMessage(PlayerMessageEvent event) {
        Player sender = event.getSender();
        Player recipient = Iterables.getLast(event.getRecipients());
        String message = event.getMessage();

        Map<String, List<String>> messageSpyMap = plugin.getUserManager().getMessageSpyMap();
        String format = ChatColor.GOLD + "[" + ChatColor.DARK_RED + "SS: " + ChatColor.YELLOW + "%1$s" + ChatColor.WHITE + " -> " + ChatColor.YELLOW + "%2$s" + ChatColor.GOLD + "] %3$s";

        for (Player target : Bukkit.getServer().getOnlinePlayers()) {
            UUID uuid = target.getUniqueId();
            String id = uuid.toString();

            if (messageSpyMap.containsKey(id)) {
                if (sender.getName().equals(target.getName())) continue;
                if (recipient.getName().equals(target.getName())) continue;

                List<String> spyingOn = messageSpyMap.get(id);
                if (spyingOn.contains(sender.getName()) || spyingOn.contains(recipient.getName())) {
                    target.sendMessage(String.format(Locale.ENGLISH, format, sender.getName(), recipient.getName(), message));
                }
            }
        }
    }
}
